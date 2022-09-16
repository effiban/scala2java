package effiban.scala2java.traversers

import effiban.scala2java.entities.EnclosingDelimiter.Parentheses
import effiban.scala2java.entities.ListTraversalOptions
import effiban.scala2java.matchers.CombinedMatchers.eqTreeList
import effiban.scala2java.matchers.TreeMatcher.eqTree
import effiban.scala2java.stubbers.OutputWriterStubber.doWrite
import effiban.scala2java.testsuites.UnitTestSuite
import org.mockito.ArgumentMatchers

import scala.meta.{Init, Name, Term, Type}

class InitTraverserImplTest extends UnitTestSuite {

  private val TypeName: Type.Name = Type.Name("MyType")
  private val ArgList1 = List(Term.Name("arg1"), Term.Name("arg2"))
  private val ArgList2 = List(Term.Name("arg3"), Term.Name("arg4"))

  private val ExpectedTraversalOptions = ListTraversalOptions(maybeEnclosingDelimiter = Some(Parentheses))

  private val typeTraverser = mock[TypeTraverser]
  private val termListTraverser = mock[TermListTraverser]

  private val initTraverser = new InitTraverserImpl(typeTraverser, termListTraverser)

  test("traverse() with no arguments and not ignored") {
    val init = Init(tpe = TypeName, name = Name.Anonymous(), argss = Nil)

    doWrite("MyType").when(typeTraverser).traverse(eqTree(TypeName))

    initTraverser.traverse(init)

    outputWriter.toString shouldBe "MyType"

    verify(termListTraverser).traverse(
      ArgumentMatchers.eq(Nil),
      ArgumentMatchers.eq(ExpectedTraversalOptions)
    )
  }

  test("traverse() for no arguments when ignored") {
    val init = Init(tpe = TypeName, name = Name.Anonymous(), argss = Nil)

    doWrite("MyType").when(typeTraverser).traverse(eqTree(TypeName))

    initTraverser.traverse(init, ignoreArgs = true)

    outputWriter.toString shouldBe "MyType"

    verifyNoMoreInteractions(termListTraverser)
  }

  test("traverse() for one argument list when not ignored") {
    val init = Init(tpe = TypeName, name = Name.Anonymous(), argss = List(ArgList1))

    doWrite("MyType").when(typeTraverser).traverse(eqTree(TypeName))
    doWrite(
      """(arg1,
        |arg2)""".stripMargin)
      .when(termListTraverser).traverse(
      eqTreeList(ArgList1),
      ArgumentMatchers.eq(ExpectedTraversalOptions)
    )

    initTraverser.traverse(init)

    outputWriter.toString shouldBe
      """MyType(arg1,
        |arg2)""".stripMargin
  }

  test("traverse() for one argument list when ignored") {
    val init = Init(tpe = TypeName, name = Name.Anonymous(), argss = List(ArgList1))

    doWrite("MyType").when(typeTraverser).traverse(eqTree(TypeName))

    initTraverser.traverse(init, ignoreArgs = true)

    outputWriter.toString shouldBe "MyType"

    verifyNoMoreInteractions(termListTraverser)
  }

  test("traverse() for two argument lists when not ignored, should concat them") {

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
      .when(termListTraverser).traverse(
      eqTreeList(ArgList1 ++ ArgList2),
      ArgumentMatchers.eq(ExpectedTraversalOptions)
    )

    initTraverser.traverse(init)

    outputWriter.toString shouldBe
      """MyType(arg1,
        |arg2,
        |arg3,
        |arg4)""".stripMargin
  }
}
