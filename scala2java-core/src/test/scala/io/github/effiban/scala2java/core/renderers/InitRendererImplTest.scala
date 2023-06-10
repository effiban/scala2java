package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.contexts.{ArgumentListContext, InitContext}
import io.github.effiban.scala2java.core.entities.EnclosingDelimiter.Parentheses
import io.github.effiban.scala2java.core.entities.ListTraversalOptions
import io.github.effiban.scala2java.core.matchers.ArgumentListContextMatcher.eqArgumentListContext
import io.github.effiban.scala2java.core.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.CombinedMatchers.eqTreeList
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchersSugar.{any, eqTo}
import org.mockito.captor.ArgCaptor

import scala.meta.{Init, Name, Term, XtensionQuasiquoteType}

class InitRendererImplTest extends UnitTestSuite {

  private val TypeName = t"MyType"
  private val ArgList1 = List(Term.Name("arg1"), Term.Name("arg2"))
  private val ArgList2 = List(Term.Name("arg3"), Term.Name("arg4"))

  private val typeRenderer = mock[TypeRenderer]
  private val argumentListRenderer = mock[ArgumentListRenderer]
  private val invocationArgRenderer = mock[ArgumentRenderer[Term]]

  private val argRendererProviderCaptor = ArgCaptor[Int => ArgumentRenderer[Term]]

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
      argRendererProviderCaptor.capture,
      eqArgumentListContext(expectedArgListContext)
    )

    argRendererProviderCaptor.value(0) shouldBe invocationArgRenderer
  }

  test("render() with no arguments, traverseEmpty = true and the rest default") {
    val init = Init(tpe = TypeName, name = Name.Anonymous(), argss = Nil)

    doWrite("MyType()").when(typeRenderer).render(eqTree(TypeName))

    initRenderer.render(init, InitContext(traverseEmpty = true))

    val expectedOptions = ListTraversalOptions(
      traverseEmpty = true,
      maybeEnclosingDelimiter = Some(Parentheses)
    )
    val expectedArgListContext = ArgumentListContext(options = expectedOptions)

    outputWriter.toString shouldBe "MyType()"

    verify(argumentListRenderer).render(
      eqTo(Nil),
      argRendererProviderCaptor.capture,
      eqArgumentListContext(expectedArgListContext)
    )

    argRendererProviderCaptor.value(0) shouldBe invocationArgRenderer
  }

  test("render() for no arguments when ignored") {
    val init = Init(tpe = TypeName, name = Name.Anonymous(), argss = Nil)

    doWrite("MyType").when(typeRenderer).render(eqTree(TypeName))

    initRenderer.render(init, InitContext(ignoreArgs = true))

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
      any[Int => ArgumentRenderer[Term]],
      eqArgumentListContext(expectedArgListContext)
    )

    initRenderer.render(init)

    outputWriter.toString shouldBe
      """MyType(arg1,
        |arg2)""".stripMargin

    verify(argumentListRenderer).render(
      eqTreeList(ArgList1),
      argRendererProviderCaptor.capture,
      eqArgumentListContext(expectedArgListContext)
    )

    argRendererProviderCaptor.value(0) shouldBe invocationArgRenderer
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
      any[Int => ArgumentRenderer[Term]],
      eqArgumentListContext(expectedArgListContext)
    )

    initRenderer.render(init, InitContext(argNameAsComment = true))

    outputWriter.toString shouldBe
      """MyType(/*arg1Name = */arg1,
        |/*arg2Name = */arg2)""".stripMargin

    verify(argumentListRenderer).render(
      eqTreeList(ArgList1),
      argRendererProviderCaptor.capture,
      eqArgumentListContext(expectedArgListContext)
    )

    argRendererProviderCaptor.value(0) shouldBe invocationArgRenderer
  }

  test("render() for one argument list when ignored") {
    val init = Init(tpe = TypeName, name = Name.Anonymous(), argss = List(ArgList1))

    doWrite("MyType").when(typeRenderer).render(eqTree(TypeName))

    initRenderer.render(init, InitContext(ignoreArgs = true))

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
      any[Int => ArgumentRenderer[Term]],
      eqArgumentListContext(expectedArgListContext)
    )

    initRenderer.render(init)

    outputWriter.toString shouldBe
      """MyType(arg1,
        |arg2,
        |arg3,
        |arg4)""".stripMargin

    verify(argumentListRenderer).render(
      eqTreeList(ArgList1 ++ ArgList2),
      argRendererProviderCaptor.capture,
      eqArgumentListContext(expectedArgListContext)
    )

    argRendererProviderCaptor.value(0) shouldBe invocationArgRenderer
  }
}
