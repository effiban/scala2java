package com.effiban.scala2java

import com.effiban.scala2java.stubs.StubPatTraverser

import scala.meta.{Lit, Pat}

class AlternativeTraverserImplTest extends UnitTestSuite {

  private val alternativeTraverser = new AlternativeTraverserImpl(new StubPatTraverser)

  test("traverse") {
    alternativeTraverser.traverse(Pat.Alternative(Lit.Int(3), Lit.Int(4)))

    outputWriter.toString shouldBe "3, 4"
  }
}
