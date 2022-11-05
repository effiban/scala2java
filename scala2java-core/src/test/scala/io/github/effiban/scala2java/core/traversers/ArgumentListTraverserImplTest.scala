package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.entities.EnclosingDelimiter.Parentheses
import io.github.effiban.scala2java.core.entities.ListTraversalOptions
import io.github.effiban.scala2java.core.matchers.TreeMatcher.eqTree
import io.github.effiban.scala2java.core.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite

import scala.meta.Term

class ArgumentListTraverserImplTest extends UnitTestSuite {

  private val arg1 = Term.Name("arg1")
  private val arg2 = Term.Name("arg2")
  private val arg3 = Term.Name("arg3")

  private val argumentTraverser = mock[ScalaTreeTraverser[Term]]

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
      ListTraversalOptions(onSameLine = true)
    )

    outputWriter.toString shouldBe "arg1"
  }

  test("traverse() when two args, single-line and no wrapping delimiter") {
    doWrite("arg1").when(argumentTraverser).traverse(eqTree(arg1))
    doWrite("arg2").when(argumentTraverser).traverse(eqTree(arg2))

    argumentListTraverser.traverse(
      args = List(arg1, arg2),
      argTraverser = argumentTraverser,
      ListTraversalOptions(onSameLine = true)
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
      ListTraversalOptions(onSameLine = true)
    )

    outputWriter.toString shouldBe "arg1, arg2, arg3"
  }

  test("traverse() when no args, traverseEmpty=false, single-line and parentheses") {
    argumentListTraverser.traverse(
      args = List.empty,
      argTraverser = argumentTraverser,
      ListTraversalOptions(onSameLine = true, maybeEnclosingDelimiter = Some(Parentheses))
    )

    outputWriter.toString shouldBe ""

    verifyNoMoreInteractions(argumentTraverser)
  }

  test("traverse() when no args, traverseEmpty=true, single-line and parentheses") {
    argumentListTraverser.traverse(
      args = List.empty,
      argTraverser = argumentTraverser,
      ListTraversalOptions(onSameLine = true, maybeEnclosingDelimiter = Some(Parentheses), traverseEmpty = true)
    )

    outputWriter.toString shouldBe "()"

    verifyNoMoreInteractions(argumentTraverser)
  }

  test("traverse() when one arg, single-line and parentheses") {
    doWrite("arg1").when(argumentTraverser).traverse(eqTree(arg1))

    argumentListTraverser.traverse(
      args = List(arg1),
      argTraverser = argumentTraverser,
      ListTraversalOptions(onSameLine = true, maybeEnclosingDelimiter = Some(Parentheses))
    )

    outputWriter.toString shouldBe "(arg1)"
  }

  test("traverse() when two args, single-line and parentheses") {
    doWrite("arg1").when(argumentTraverser).traverse(eqTree(arg1))
    doWrite("arg2").when(argumentTraverser).traverse(eqTree(arg2))

    argumentListTraverser.traverse(
      args = List(arg1, arg2),
      argTraverser = argumentTraverser,
      ListTraversalOptions(onSameLine = true, maybeEnclosingDelimiter = Some(Parentheses))
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
      ListTraversalOptions(onSameLine = true, maybeEnclosingDelimiter = Some(Parentheses))
    )

    outputWriter.toString shouldBe "(arg1, arg2, arg3)"
  }
}
