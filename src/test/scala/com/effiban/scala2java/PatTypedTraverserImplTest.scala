package com.effiban.scala2java

import com.effiban.scala2java.stubs.{StubPatTraverser, StubTypeTraverser}

import scala.meta.{Pat, Term, Type}

class PatTypedTraverserImplTest extends UnitTestSuite {

  val patTypedTraverser = new PatTypedTraverserImpl(new StubTypeTraverser(), new StubPatTraverser())

  test("traverse()") {
    patTypedTraverser.traverse(Pat.Typed(lhs = Pat.Var(Term.Name("x")), rhs = Type.Name("MyType")))

    outputWriter.toString shouldBe "MyType x"
  }
}
