package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.{InitContext, InvocationArgListContext}
import io.github.effiban.scala2java.core.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.CombinedMatchers.eqTreeList
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchers

import scala.meta.{Init, Name, Term, Type}

class InitTraverserImplTest extends UnitTestSuite {

  private val TypeName: Type.Name = Type.Name("MyType")
  private val ArgList1 = List(Term.Name("arg1"), Term.Name("arg2"))
  private val ArgList2 = List(Term.Name("arg3"), Term.Name("arg4"))

  private val typeTraverser = mock[TypeTraverser]
  private val invocationArgListTraverser = mock[InvocationArgListTraverser]

  private val initTraverser = new InitTraverserImpl(typeTraverser, invocationArgListTraverser)

  test("traverse() with no arguments and default context") {
    val init = Init(tpe = TypeName, name = Name.Anonymous(), argss = Nil)

    doWrite("MyType").when(typeTraverser).traverse(eqTree(TypeName))

    initTraverser.traverse(init)

    outputWriter.toString shouldBe "MyType"

    verify(invocationArgListTraverser).traverse(
      ArgumentMatchers.eq(Nil),
      ArgumentMatchers.eq(InvocationArgListContext())
    )
  }

  test("traverse() with no arguments, traverseEmpty = true and the rest default") {
    val init = Init(tpe = TypeName, name = Name.Anonymous(), argss = Nil)

    doWrite("MyType()").when(typeTraverser).traverse(eqTree(TypeName))

    initTraverser.traverse(init, InitContext(traverseEmpty = true))

    outputWriter.toString shouldBe "MyType()"

    verify(invocationArgListTraverser).traverse(
      ArgumentMatchers.eq(Nil),
      ArgumentMatchers.eq(InvocationArgListContext(traverseEmpty = true))
    )
  }

  test("traverse() for no arguments when ignored") {
    val init = Init(tpe = TypeName, name = Name.Anonymous(), argss = Nil)

    doWrite("MyType").when(typeTraverser).traverse(eqTree(TypeName))

    initTraverser.traverse(init, InitContext(ignoreArgs = true))

    outputWriter.toString shouldBe "MyType"

    verifyNoMoreInteractions(invocationArgListTraverser)
  }

  test("traverse() for one argument list with defaults") {
    val init = Init(tpe = TypeName, name = Name.Anonymous(), argss = List(ArgList1))

    doWrite("MyType").when(typeTraverser).traverse(eqTree(TypeName))
    doWrite(
      """(arg1,
        |arg2)""".stripMargin)
      .when(invocationArgListTraverser).traverse(
      eqTreeList(ArgList1),
      ArgumentMatchers.eq(InvocationArgListContext())
    )

    initTraverser.traverse(init)

    outputWriter.toString shouldBe
      """MyType(arg1,
        |arg2)""".stripMargin
  }

  test("traverse() for one argument list when argNameAsComment=true and the rest default") {
    val init = Init(tpe = TypeName, name = Name.Anonymous(), argss = List(ArgList1))

    doWrite("MyType").when(typeTraverser).traverse(eqTree(TypeName))
    doWrite(
      """(/*arg1Name = */arg1,
        |/*arg2Name = */arg2)""".stripMargin)
      .when(invocationArgListTraverser).traverse(
      eqTreeList(ArgList1),
      ArgumentMatchers.eq(InvocationArgListContext(argNameAsComment = true))
    )

    initTraverser.traverse(init, InitContext(argNameAsComment = true))

    outputWriter.toString shouldBe
      """MyType(/*arg1Name = */arg1,
        |/*arg2Name = */arg2)""".stripMargin
  }

  test("traverse() for one argument list when ignored") {
    val init = Init(tpe = TypeName, name = Name.Anonymous(), argss = List(ArgList1))

    doWrite("MyType").when(typeTraverser).traverse(eqTree(TypeName))

    initTraverser.traverse(init, InitContext(ignoreArgs = true))

    outputWriter.toString shouldBe "MyType"

    verifyNoMoreInteractions(invocationArgListTraverser)
  }

  test("traverse() for two argument lists with default context, should concat them") {

    val init = Init(
      tpe = TypeName,
      name = Name.Anonymous(),
      argss = List(ArgList1, ArgList2)
    )

    doWrite("MyType").when(typeTraverser).traverse(eqTree(TypeName))
    doWrite(
      """(arg1,
        |arg2,
        |arg3,
        |arg4)""".stripMargin)
      .when(invocationArgListTraverser).traverse(
      eqTreeList(ArgList1 ++ ArgList2),
      ArgumentMatchers.eq(InvocationArgListContext())
    )

    initTraverser.traverse(init)

    outputWriter.toString shouldBe
      """MyType(arg1,
        |arg2,
        |arg3,
        |arg4)""".stripMargin
  }
}
