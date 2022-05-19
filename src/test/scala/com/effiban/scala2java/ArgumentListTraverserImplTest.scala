package com.effiban.scala2java

import com.effiban.scala2java.stubs.StubTermTraverser

import scala.meta.Term

class ArgumentListTraverserImplTest extends UnitTestSuite {

  private val argumentListTraverser = new ArgumentListTraverserImpl()

  private val argumentTraverser = new StubTermTraverser()

  test("traverse() when one arg, multi-line and no wrapping delimiter") {
    argumentListTraverser.traverse(
      args = List(Term.Name("arg1")),
      argTraverser = argumentTraverser
    )

    outputWriter.toString shouldBe "arg1"
  }

  test("traverse() when two args, multi-line and no wrapping delimiter") {
    argumentListTraverser.traverse(
      args = List(Term.Name("arg1"), Term.Name("arg2")),
      argTraverser = argumentTraverser
    )

    outputWriter.toString shouldBe "arg1, arg2"
  }

  test("traverse() when three args, multi-line and no wrapping delimiter") {
    argumentListTraverser.traverse(
      args = List(Term.Name("arg1"), Term.Name("arg2"), Term.Name("arg3")),
      argTraverser = argumentTraverser
    )

    outputWriter.toString shouldBe
      """arg1,
        |arg2,
        |arg3""".stripMargin
  }

  test("traverse() when one arg, single-line and no wrapping delimiter") {
    argumentListTraverser.traverse(
      args = List(Term.Name("arg1")),
      argTraverser = argumentTraverser,
      onSameLine = true
    )

    outputWriter.toString shouldBe "arg1"
  }

  test("traverse() when two args, single-line and no wrapping delimiter") {
    argumentListTraverser.traverse(
      args = List(Term.Name("arg1"), Term.Name("arg2")),
      argTraverser = argumentTraverser,
      onSameLine = true
    )

    outputWriter.toString shouldBe "arg1, arg2"
  }

  test("traverse() when three args, single-line and no wrapping delimiter") {
    argumentListTraverser.traverse(
      args = List(Term.Name("arg1"), Term.Name("arg2"), Term.Name("arg3")),
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
  }

  test("traverse() when one arg, single-line and parentheses") {
    argumentListTraverser.traverse(
      args = List(Term.Name("arg1")),
      argTraverser = argumentTraverser,
      onSameLine = true,
      maybeWrappingDelimiterType = Some(Parentheses)
    )

    outputWriter.toString shouldBe "(arg1)"
  }

  test("traverse() when two args, single-line and parentheses") {
    argumentListTraverser.traverse(
      args = List(Term.Name("arg1"), Term.Name("arg2")),
      argTraverser = argumentTraverser,
      onSameLine = true,
      maybeWrappingDelimiterType = Some(Parentheses)
    )

    outputWriter.toString shouldBe "(arg1, arg2)"
  }

  test("traverse() when three args, single-line and parentheses") {
    argumentListTraverser.traverse(
      args = List(Term.Name("arg1"), Term.Name("arg2"), Term.Name("arg3")),
      argTraverser = argumentTraverser,
      onSameLine = true,
      maybeWrappingDelimiterType = Some(Parentheses)
    )

    outputWriter.toString shouldBe "(arg1, arg2, arg3)"
  }
}
