package com.effiban.scala2java

import com.effiban.scala2java.stubs.{StubTermNameTraverser, StubTermTraverser}

import scala.meta.Term

class ApplyUnaryTraverserImplTest extends UnitTestSuite {

  private val applyUnaryTraverser = new ApplyUnaryTraverserImpl(new StubTermNameTraverser, new StubTermTraverser)

  test("traverse") {
    applyUnaryTraverser.traverse(Term.ApplyUnary(op = Term.Name("!"), arg = Term.Name("myFlag")))

    outputWriter.toString shouldBe "!myFlag"
  }
}
