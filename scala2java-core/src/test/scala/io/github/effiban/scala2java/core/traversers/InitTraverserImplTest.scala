package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.{ArgumentListContext, InitContext}
import io.github.effiban.scala2java.core.entities.EnclosingDelimiter.Parentheses
import io.github.effiban.scala2java.core.entities.ListTraversalOptions
import io.github.effiban.scala2java.core.matchers.ArgumentListContextMatcher.eqArgumentListContext
import io.github.effiban.scala2java.core.renderers.TypeRenderer
import io.github.effiban.scala2java.core.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.CombinedMatchers.eqTreeList
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchersSugar.eqTo

import scala.meta.{Init, Name, Term, XtensionQuasiquoteType}

class InitTraverserImplTest extends UnitTestSuite {

  private val TypeName = t"MyType"
  private val TraversedTypeName = t"MyTraversedType"
  private val ArgList1 = List(Term.Name("arg1"), Term.Name("arg2"))
  private val ArgList2 = List(Term.Name("arg3"), Term.Name("arg4"))

  private val typeTraverser = mock[TypeTraverser]
  private val typeRenderer = mock[TypeRenderer]
  private val argumentListTraverser = mock[ArgumentListTraverser]
  private val invocationArgTraverser = mock[ArgumentTraverser[Term]]

  private val initTraverser = new InitTraverserImpl(
    typeTraverser,
    typeRenderer,
    argumentListTraverser,
    invocationArgTraverser
  )

  test("traverse() with no arguments and default context") {
    val init = Init(tpe = TypeName, name = Name.Anonymous(), argss = Nil)

    val expectedOptions = ListTraversalOptions(maybeEnclosingDelimiter = Some(Parentheses))
    val expectedArgListContext = ArgumentListContext(options = expectedOptions)

    doReturn(TraversedTypeName).when(typeTraverser).traverse(eqTree(TypeName))
    doWrite("MyTraversedType").when(typeRenderer).render(eqTree(TraversedTypeName))

    initTraverser.traverse(init)

    outputWriter.toString shouldBe "MyTraversedType"

    verify(argumentListTraverser).traverse(
      eqTo(Nil),
      eqTo(invocationArgTraverser),
      eqArgumentListContext(expectedArgListContext)
    )
  }

  test("traverse() with no arguments, traverseEmpty = true and the rest default") {
    val init = Init(tpe = TypeName, name = Name.Anonymous(), argss = Nil)

    doReturn(TraversedTypeName).when(typeTraverser).traverse(eqTree(TypeName))
    doWrite("MyTraversedType()").when(typeRenderer).render(eqTree(TraversedTypeName))

    initTraverser.traverse(init, InitContext(traverseEmpty = true))

    val expectedOptions = ListTraversalOptions(
      traverseEmpty = true,
      maybeEnclosingDelimiter = Some(Parentheses)
    )
    val expectedArgListContext = ArgumentListContext(options = expectedOptions)

    outputWriter.toString shouldBe "MyTraversedType()"

    verify(argumentListTraverser).traverse(
      eqTo(Nil),
      eqTo(invocationArgTraverser),
      eqArgumentListContext(expectedArgListContext)
    )
  }

  test("traverse() for no arguments when ignored") {
    val init = Init(tpe = TypeName, name = Name.Anonymous(), argss = Nil)

    doReturn(TraversedTypeName).when(typeTraverser).traverse(eqTree(TypeName))
    doWrite("MyTraversedType").when(typeRenderer).render(eqTree(TraversedTypeName))

    initTraverser.traverse(init, InitContext(ignoreArgs = true))

    outputWriter.toString shouldBe "MyTraversedType"

    verifyNoMoreInteractions(argumentListTraverser)
  }

  test("traverse() for one argument list with defaults") {
    val init = Init(tpe = TypeName, name = Name.Anonymous(), argss = List(ArgList1))

    val expectedOptions = ListTraversalOptions(maybeEnclosingDelimiter = Some(Parentheses))
    val expectedArgListContext = ArgumentListContext(options = expectedOptions)

    doReturn(TraversedTypeName).when(typeTraverser).traverse(eqTree(TypeName))
    doWrite("MyTraversedType").when(typeRenderer).render(eqTree(TraversedTypeName))
    doWrite(
      """(arg1,
        |arg2)""".stripMargin)
      .when(argumentListTraverser).traverse(
      eqTreeList(ArgList1),
      eqTo(invocationArgTraverser),
      eqArgumentListContext(expectedArgListContext)
    )

    initTraverser.traverse(init)

    outputWriter.toString shouldBe
      """MyTraversedType(arg1,
        |arg2)""".stripMargin
  }

  test("traverse() for one argument list when argNameAsComment=true and the rest default") {
    val init = Init(tpe = TypeName, name = Name.Anonymous(), argss = List(ArgList1))

    val expectedOptions = ListTraversalOptions(maybeEnclosingDelimiter = Some(Parentheses))
    val expectedArgListContext = ArgumentListContext(options = expectedOptions, argNameAsComment = true)

    doReturn(TraversedTypeName).when(typeTraverser).traverse(eqTree(TypeName))
    doWrite("MyTraversedType").when(typeRenderer).render(eqTree(TraversedTypeName))
    doWrite(
      """(/*arg1Name = */arg1,
        |/*arg2Name = */arg2)""".stripMargin)
      .when(argumentListTraverser).traverse(
      eqTreeList(ArgList1),
      eqTo(invocationArgTraverser),
      eqArgumentListContext(expectedArgListContext)
    )

    initTraverser.traverse(init, InitContext(argNameAsComment = true))

    outputWriter.toString shouldBe
      """MyTraversedType(/*arg1Name = */arg1,
        |/*arg2Name = */arg2)""".stripMargin
  }

  test("traverse() for one argument list when ignored") {
    val init = Init(tpe = TypeName, name = Name.Anonymous(), argss = List(ArgList1))

    doReturn(TraversedTypeName).when(typeTraverser).traverse(eqTree(TypeName))
    doWrite("MyTraversedType").when(typeRenderer).render(eqTree(TraversedTypeName))

    initTraverser.traverse(init, InitContext(ignoreArgs = true))

    outputWriter.toString shouldBe "MyTraversedType"

    verifyNoMoreInteractions(argumentListTraverser)
  }

  test("traverse() for two argument lists with default context, should concat them") {

    val init = Init(
      tpe = TypeName,
      name = Name.Anonymous(),
      argss = List(ArgList1, ArgList2)
    )

    val expectedOptions = ListTraversalOptions(maybeEnclosingDelimiter = Some(Parentheses))
    val expectedArgListContext = ArgumentListContext(options = expectedOptions)

    doReturn(TraversedTypeName).when(typeTraverser).traverse(eqTree(TypeName))
    doWrite("MyTraversedType").when(typeRenderer).render(eqTree(TraversedTypeName))
    doWrite(
      """(arg1,
        |arg2,
        |arg3,
        |arg4)""".stripMargin)
      .when(argumentListTraverser).traverse(
      eqTreeList(ArgList1 ++ ArgList2),
      eqTo(invocationArgTraverser),
      eqArgumentListContext(expectedArgListContext)
    )

    initTraverser.traverse(init)

    outputWriter.toString shouldBe
      """MyTraversedType(arg1,
        |arg2,
        |arg3,
        |arg4)""".stripMargin
  }
}
