package com.effiban.scala2java

import com.effiban.scala2java.stubs.{StubBlockTraverser, StubTermTraverser}

import scala.meta.Term.{Apply, ApplyInfix, Block, If}
import scala.meta.{Lit, Term}

class IfTraverserImplTest extends UnitTestSuite {

  private val ifTraverser = new IfTraverserImpl(new StubTermTraverser(), new StubBlockTraverser())

  private val x: Term.Name = Term.Name("x")
  private val y: Term.Name = Term.Name("y")

  private val operation1: Term.Name = Term.Name("operation1")
  private val operation2: Term.Name = Term.Name("operation2")
  private val otherOperation1: Term.Name = Term.Name("otherOperation1")
  private val otherOperation2: Term.Name = Term.Name("otherOperation2")

  private val Condition = ApplyInfix(
    lhs = x,
    op = Term.Name("<"),
    targs = Nil,
    args = List(Lit.Int(3))
  )

  private val ThenBlock = Block(
    List(
      Apply(operation1, List(x)),
      Apply(operation2, List(y))
    )
  )

  private val ThenStatement = Apply(operation1, List(x))

  private val ElseBlock = Block(
    List(
      Apply(otherOperation1, List(x)),
      Apply(otherOperation2, List(y))
    )
  )

  private val ElseStatement = Apply(otherOperation1, List(x))


  test("traverse() when 'then' is a block, no 'else', and should not return") {
    val `if` = If(
      cond = Condition,
      thenp = ThenBlock,
      elsep = Lit.Unit()
    )

    ifTraverser.traverse(`if`)

    outputWriter.toString shouldBe
    """if (x < 3)
      |/**
      |* STUB BLOCK
      |* Scala Body:
      |* {
      |*   operation1(x)
      |*   operation2(y)
      |* }
      |*/
      |""".stripMargin
  }

  test("traverse() when 'then' is a block, no 'else', and should return") {
    val `if` = If(
      cond = Condition,
      thenp = ThenBlock,
      elsep = Lit.Unit()
    )

    ifTraverser.traverse(`if`, shouldReturnValue = true)

    outputWriter.toString shouldBe
      """if (x < 3)
        |/**
        |* STUB BLOCK
        |* Should return a value
        |* Scala Body:
        |* {
        |*   operation1(x)
        |*   operation2(y)
        |* }
        |*/
        |""".stripMargin
  }

  test("traverse() when 'then' is a single statement, no 'else' and should not return") {
    val `if` = If(
      cond = Condition,
      thenp = ThenStatement,
      elsep = Lit.Unit()
    )

    ifTraverser.traverse(`if`)

    outputWriter.toString shouldBe
      """if (x < 3)
        |/**
        |* STUB BLOCK
        |* Scala Body:
        |* {
        |*   operation1(x)
        |* }
        |*/
        |""".stripMargin
  }

  test("traverse() when 'then' is a single statement, no 'else' and should return") {
    val `if` = If(
      cond = Condition,
      thenp = ThenStatement,
      elsep = Lit.Unit()
    )

    ifTraverser.traverse(`if`, shouldReturnValue = true)

    outputWriter.toString shouldBe
      """if (x < 3)
        |/**
        |* STUB BLOCK
        |* Should return a value
        |* Scala Body:
        |* {
        |*   operation1(x)
        |* }
        |*/
        |""".stripMargin
  }

  test("traverse() when 'else' is a block, and should not return") {
    val `if` = If(
      cond = Condition,
      thenp = ThenStatement,
      elsep = ElseBlock
    )

    ifTraverser.traverse(`if`)

    outputWriter.toString shouldBe
      """if (x < 3)
        |/**
        |* STUB BLOCK
        |* Scala Body:
        |* {
        |*   operation1(x)
        |* }
        |*/
        |else
        |/**
        |* STUB BLOCK
        |* Scala Body:
        |* {
        |*   otherOperation1(x)
        |*   otherOperation2(y)
        |* }
        |*/
        |""".stripMargin
  }

  test("traverse() when 'else' is a block, and should return") {
    val `if` = If(
      cond = Condition,
      thenp = ThenStatement,
      elsep = ElseBlock
    )

    ifTraverser.traverse(`if`, shouldReturnValue = true)

    outputWriter.toString shouldBe
      """if (x < 3)
        |/**
        |* STUB BLOCK
        |* Should return a value
        |* Scala Body:
        |* {
        |*   operation1(x)
        |* }
        |*/
        |else
        |/**
        |* STUB BLOCK
        |* Should return a value
        |* Scala Body:
        |* {
        |*   otherOperation1(x)
        |*   otherOperation2(y)
        |* }
        |*/
        |""".stripMargin
  }

  test("traverse() when 'else' is a single statement, and should not return") {
    val `if` = If(
      cond = Condition,
      thenp = ThenStatement,
      elsep = ElseStatement
    )

    ifTraverser.traverse(`if`)

    outputWriter.toString shouldBe
      """if (x < 3)
        |/**
        |* STUB BLOCK
        |* Scala Body:
        |* {
        |*   operation1(x)
        |* }
        |*/
        |else
        |/**
        |* STUB BLOCK
        |* Scala Body:
        |* {
        |*   otherOperation1(x)
        |* }
        |*/
        |""".stripMargin
  }

  test("traverse() when 'else' is a single statement, and should return") {
    val `if` = If(
      cond = Condition,
      thenp = ThenStatement,
      elsep = ElseStatement
    )

    ifTraverser.traverse(`if`, shouldReturnValue = true)

    outputWriter.toString shouldBe
      """if (x < 3)
        |/**
        |* STUB BLOCK
        |* Should return a value
        |* Scala Body:
        |* {
        |*   operation1(x)
        |* }
        |*/
        |else
        |/**
        |* STUB BLOCK
        |* Should return a value
        |* Scala Body:
        |* {
        |*   otherOperation1(x)
        |* }
        |*/
        |""".stripMargin
  }
}
