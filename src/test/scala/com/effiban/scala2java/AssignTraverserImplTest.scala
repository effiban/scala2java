package com.effiban.scala2java

import com.effiban.scala2java.stubs.StubTermTraverser

import scala.meta.{Lit, Term}

class AssignTraverserImplTest extends UnitTestSuite {

  private val assignTraverser = new AssignTraverserImpl(new StubTermTraverser)

  test("traverse") {
    assignTraverser.traverse(Term.Assign(lhs = Term.Name("myVal"), rhs = Lit.Int(3)))

    outputWriter.toString shouldBe "myVal = 3"
  }
}
