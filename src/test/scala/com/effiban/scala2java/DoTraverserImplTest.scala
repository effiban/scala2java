package com.effiban.scala2java

import com.effiban.scala2java.stubs.{StubBlockTraverser, StubTermTraverser}

import scala.meta.Term.{ApplyInfix, Block, Do}
import scala.meta.{Lit, Term}

class DoTraverserImplTest extends UnitTestSuite {

  private val doTraverser = new DoTraverserImpl(new StubTermTraverser(), new StubBlockTraverser())

  test("traverse() when body is single statement") {
    val varX = Term.Name("x")

    val `do` = Do(
      body = Term.Apply(
        fun = Term.Name("someOperation"),
        args = List(varX)),
      expr = ApplyInfix(
        lhs = varX,
        op = Term.Name("<"),
        targs = Nil,
        args = List(Lit.Int(3)))
    )

    doTraverser.traverse(`do`)

    outputWriter.toString shouldBe
      """do
        |/**
        |* STUB BLOCK
        |* Scala Body:
        |* {
        |*   someOperation(x)
        |* }
        |*/
        | while (x < 3)""".stripMargin
  }

  test("traverse() when body is a block") {
    val varX = Term.Name("x")

    val `do` = Do(
      body = Block(
        List(
          Term.Apply(
            fun = Term.Name("someOperation"),
            args = List(varX))
        )
      ),
      expr = ApplyInfix(
        lhs = varX,
        op = Term.Name("<"),
        targs = Nil,
        args = List(Lit.Int(3)))
    )

    doTraverser.traverse(`do`)

    outputWriter.toString shouldBe
      """do
        |/**
        |* STUB BLOCK
        |* Scala Body:
        |* {
        |*   someOperation(x)
        |* }
        |*/
        | while (x < 3)""".stripMargin
  }
}
