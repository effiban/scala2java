package com.effiban.scala2java

import com.effiban.scala2java.stubs.{StubArgumentListTraverser, StubPatTraverser}

import scala.meta.{Pat, Term}

class PatListTraverserImplTest extends UnitTestSuite {

  private val patListTraverser = new PatListTraverserImpl(new StubArgumentListTraverser(), new StubPatTraverser())


  test("traverse() when no pats") {
    patListTraverser.traverse(Nil)

    outputWriter.toString shouldBe ""
  }

  test("traverse() when one pat") {

    val pat = Pat.Var(Term.Name("x"))

    patListTraverser.traverse(List(pat))

    outputWriter.toString shouldBe "x"
  }

  test("traverse() when two pats") {
    val pat1 = Pat.Var(Term.Name("x"))
    val pat2 = Pat.Var(Term.Name("y"))

    patListTraverser.traverse(List(pat1, pat2))

    outputWriter.toString shouldBe "x, y"
  }
}
