package com.effiban.scala2java

import com.effiban.scala2java.stubs.{StubTermTraverser, StubTypeTraverser}

import scala.meta.{Lit, Term, Type}

class AscribeTraverserImplTest extends UnitTestSuite {

  private val ascribeTraverser = new AscribeTraverserImpl(new StubTypeTraverser, new StubTermTraverser)

  test("traverse") {
    ascribeTraverser.traverse(Term.Ascribe(expr = Lit.Int(22), tpe = Type.Name("MyType")))

    outputWriter.toString shouldBe "(MyType)22"
  }
}
