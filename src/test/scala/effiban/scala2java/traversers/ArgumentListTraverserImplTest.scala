package effiban.scala2java.traversers

import effiban.scala2java.matchers.TreeMatcher.eqTree
import effiban.scala2java.stubbers.OutputWriterStubber.doWrite
import effiban.scala2java.{Parentheses, UnitTestSuite}

import scala.meta.Term

class ArgumentListTraverserImplTest extends UnitTestSuite {

  private val arg1 = Term.Name("arg1")
  private val arg2 = Term.Name("arg2")
  private val arg3 = Term.Name("arg3")

  private val argumentTraverser = mock[TermTraverser]

  private val argumentListTraverser = new ArgumentListTraverserImpl()


  test("traverse() when one arg, multi-line and no wrapping delimiter") {
    doWrite("arg1").when(argumentTraverser).traverse(eqTree(arg1))

    argumentListTraverser.traverse(args = List(arg1), argTraverser = argumentTraverser)

    outputWriter.toString shouldBe "arg1"
  }

  test("traverse() when two args, multi-line and no wrapping delimiter") {
    doWrite("arg1").when(argumentTraverser).traverse(eqTree(arg1))
    doWrite("arg2").when(argumentTraverser).traverse(eqTree(arg2))

    argumentListTraverser.traverse(args = List(arg1, arg2), argTraverser = argumentTraverser)

    outputWriter.toString shouldBe "arg1, arg2"
  }

  test("traverse() when three args, multi-line and no wrapping delimiter") {
    doWrite("arg1").when(argumentTraverser).traverse(eqTree(arg1))
    doWrite("arg2").when(argumentTraverser).traverse(eqTree(arg2))
    doWrite("arg3").when(argumentTraverser).traverse(eqTree(arg3))

    argumentListTraverser.traverse(
      args = List(arg1, arg2, arg3),
      argTraverser = argumentTraverser
    )

    outputWriter.toString shouldBe
      """arg1,
        |arg2,
        |arg3""".stripMargin
  }

  test("traverse() when one arg, single-line and no wrapping delimiter") {
    doWrite("arg1").when(argumentTraverser).traverse(eqTree(arg1))

    argumentListTraverser.traverse(
      args = List(arg1),
      argTraverser = argumentTraverser,
      onSameLine = true
    )

    outputWriter.toString shouldBe "arg1"
  }

  test("traverse() when two args, single-line and no wrapping delimiter") {
    doWrite("arg1").when(argumentTraverser).traverse(eqTree(arg1))
    doWrite("arg2").when(argumentTraverser).traverse(eqTree(arg2))

    argumentListTraverser.traverse(
      args = List(arg1, arg2),
      argTraverser = argumentTraverser,
      onSameLine = true
    )

    outputWriter.toString shouldBe "arg1, arg2"
  }

  test("traverse() when three args, single-line and no wrapping delimiter") {
    doWrite("arg1").when(argumentTraverser).traverse(eqTree(arg1))
    doWrite("arg2").when(argumentTraverser).traverse(eqTree(arg2))
    doWrite("arg3").when(argumentTraverser).traverse(eqTree(arg3))

    argumentListTraverser.traverse(
      args = List(arg1, arg2, arg3),
      argTraverser = argumentTraverser,
      onSameLine = true
    )

    outputWriter.toString shouldBe "arg1, arg2, arg3"
  }

  test("traverse() when no args, single-line and parentheses") {
    argumentListTraverser.traverse(
      args = List.empty,
      argTraverser = argumentTraverser,
      onSameLine = true,
      maybeWrappingDelimiterType = Some(Parentheses)
    )

    outputWriter.toString shouldBe "()"

    verifyNoMoreInteractions(argumentTraverser)
  }

  test("traverse() when one arg, single-line and parentheses") {
    doWrite("arg1").when(argumentTraverser).traverse(eqTree(arg1))

    argumentListTraverser.traverse(
      args = List(arg1),
      argTraverser = argumentTraverser,
      onSameLine = true,
      maybeWrappingDelimiterType = Some(Parentheses)
    )

    outputWriter.toString shouldBe "(arg1)"
  }

  test("traverse() when two args, single-line and parentheses") {
    doWrite("arg1").when(argumentTraverser).traverse(eqTree(arg1))
    doWrite("arg2").when(argumentTraverser).traverse(eqTree(arg2))

    argumentListTraverser.traverse(
      args = List(arg1, arg2),
      argTraverser = argumentTraverser,
      onSameLine = true,
      maybeWrappingDelimiterType = Some(Parentheses)
    )

    outputWriter.toString shouldBe "(arg1, arg2)"
  }

  test("traverse() when three args, single-line and parentheses") {
    doWrite("arg1").when(argumentTraverser).traverse(eqTree(arg1))
    doWrite("arg2").when(argumentTraverser).traverse(eqTree(arg2))
    doWrite("arg3").when(argumentTraverser).traverse(eqTree(arg3))

    argumentListTraverser.traverse(
      args = List(arg1, arg2, arg3),
      argTraverser = argumentTraverser,
      onSameLine = true,
      maybeWrappingDelimiterType = Some(Parentheses)
    )

    outputWriter.toString shouldBe "(arg1, arg2, arg3)"
  }
}
