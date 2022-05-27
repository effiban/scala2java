package com.effiban.scala2java

import com.effiban.scala2java.stubs.StubTermNameTraverser

import scala.meta.{Pat, Term}

class PatVarTraverserImplTest extends UnitTestSuite {

  val patVarTraverser = new PatVarTraverserImpl(new StubTermNameTraverser())

  test("traverse()") {
    patVarTraverser.traverse(Pat.Var(Term.Name("x")))

    outputWriter.toString shouldBe "x"
  }
}
