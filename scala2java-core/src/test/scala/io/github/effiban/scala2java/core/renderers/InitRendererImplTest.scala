package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.contexts.ArgumentListContext
import io.github.effiban.scala2java.core.entities.EnclosingDelimiter.Parentheses
import io.github.effiban.scala2java.core.entities.ListTraversalOptions
import io.github.effiban.scala2java.core.matchers.ArgumentListContextMatcher.eqArgumentListContext
import io.github.effiban.scala2java.core.renderers.contexts.InitRenderContext
import io.github.effiban.scala2java.core.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.CombinedMatchers.eqTreeList
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchersSugar.eqTo

import scala.meta.{Init, Name, Term, XtensionQuasiquoteType}

class InitRendererImplTest extends UnitTestSuite {

  private val TypeName = t"MyType"
  private val ArgList1 = List(Term.Name("arg1"), Term.Name("arg2"))
  private val ArgList2 = List(Term.Name("arg3"), Term.Name("arg4"))

  private val typeRenderer = mock[TypeRenderer]
  private val argumentListRenderer = mock[ArgumentListRenderer]
  private val invocationArgRenderer = mock[ArgumentRenderer[Term]]

  private val initRenderer = new InitRendererImpl(
    typeRenderer,
    argumentListRenderer,
    invocationArgRenderer
  )

  test("render() with no arguments and default context") {
    val init = Init(tpe = TypeName, name = Name.Anonymous(), argss = Nil)

    val expectedOptions = ListTraversalOptions(maybeEnclosingDelimiter = Some(Parentheses))
    val expectedArgListContext = ArgumentListContext(options = expectedOptions)

    doWrite("MyType").when(typeRenderer).render(eqTree(TypeName))

    initRenderer.render(init)

    outputWriter.toString shouldBe "MyType"

    verify(argumentListRenderer).render(
      eqTo(Nil),
      eqTo(invocationArgRenderer),
      eqArgumentListContext(expectedArgListContext)
    )
  }

  test("render() with no arguments, traverseEmpty = true and the rest default") {
    val init = Init(tpe = TypeName, name = Name.Anonymous(), argss = Nil)

    doWrite("MyType()").when(typeRenderer).render(eqTree(TypeName))

    initRenderer.render(init, InitRenderContext(renderEmpty = true))

    val expectedOptions = ListTraversalOptions(
      traverseEmpty = true,
      maybeEnclosingDelimiter = Some(Parentheses)
    )
    val expectedArgListContext = ArgumentListContext(options = expectedOptions)

    outputWriter.toString shouldBe "MyType()"

    verify(argumentListRenderer).render(
      eqTo(Nil),
      eqTo(invocationArgRenderer),
      eqArgumentListContext(expectedArgListContext)
    )
  }

  test("render() for no arguments when ignored") {
    val init = Init(tpe = TypeName, name = Name.Anonymous(), argss = Nil)

    doWrite("MyType").when(typeRenderer).render(eqTree(TypeName))

    initRenderer.render(init, InitRenderContext(ignoreArgs = true))

    outputWriter.toString shouldBe "MyType"

    verifyNoMoreInteractions(argumentListRenderer)
  }

  test("render() for one argument list with defaults") {
    val init = Init(tpe = TypeName, name = Name.Anonymous(), argss = List(ArgList1))

    val expectedOptions = ListTraversalOptions(maybeEnclosingDelimiter = Some(Parentheses))
    val expectedArgListContext = ArgumentListContext(options = expectedOptions)

    doWrite("MyType").when(typeRenderer).render(eqTree(TypeName))
    doWrite(
      """(arg1,
        |arg2)""".stripMargin)
      .when(argumentListRenderer).render(
      eqTreeList(ArgList1),
      eqTo(invocationArgRenderer),
      eqArgumentListContext(expectedArgListContext)
    )

    initRenderer.render(init)

    outputWriter.toString shouldBe
      """MyType(arg1,
        |arg2)""".stripMargin
  }

  test("render() for one argument list when argNameAsComment=true and the rest default") {
    val init = Init(tpe = TypeName, name = Name.Anonymous(), argss = List(ArgList1))

    val expectedOptions = ListTraversalOptions(maybeEnclosingDelimiter = Some(Parentheses))
    val expectedArgListContext = ArgumentListContext(options = expectedOptions, argNameAsComment = true)

    doWrite("MyType").when(typeRenderer).render(eqTree(TypeName))
    doWrite(
      """(/*arg1Name = */arg1,
        |/*arg2Name = */arg2)""".stripMargin)
      .when(argumentListRenderer).render(
      eqTreeList(ArgList1),
      eqTo(invocationArgRenderer),
      eqArgumentListContext(expectedArgListContext)
    )

    initRenderer.render(init, InitRenderContext(argNameAsComment = true))

    outputWriter.toString shouldBe
      """MyType(/*arg1Name = */arg1,
        |/*arg2Name = */arg2)""".stripMargin
  }

  test("render() for one argument list when ignored") {
    val init = Init(tpe = TypeName, name = Name.Anonymous(), argss = List(ArgList1))

    doWrite("MyType").when(typeRenderer).render(eqTree(TypeName))

    initRenderer.render(init, InitRenderContext(ignoreArgs = true))

    outputWriter.toString shouldBe "MyType"

    verifyNoMoreInteractions(argumentListRenderer)
  }

  test("render() for two argument lists with default context, should concat them") {

    val init = Init(
      tpe = TypeName,
      name = Name.Anonymous(),
      argss = List(ArgList1, ArgList2)
    )

    val expectedOptions = ListTraversalOptions(maybeEnclosingDelimiter = Some(Parentheses))
    val expectedArgListContext = ArgumentListContext(options = expectedOptions)

    doWrite("MyType").when(typeRenderer).render(eqTree(TypeName))
    doWrite(
      """(arg1,
        |arg2,
        |arg3,
        |arg4)""".stripMargin)
      .when(argumentListRenderer).render(
      eqTreeList(ArgList1 ++ ArgList2),
      eqTo(invocationArgRenderer),
      eqArgumentListContext(expectedArgListContext)
    )

    initRenderer.render(init)

    outputWriter.toString shouldBe
      """MyType(arg1,
        |arg2,
        |arg3,
        |arg4)""".stripMargin
  }
}
