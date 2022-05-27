package com.effiban.scala2java

import com.effiban.scala2java.stubs.StubTermTraverser

import scala.meta.Term
import scala.meta.Term.Eta

class EtaTraverserImplTest extends UnitTestSuite {

  val etaTraverser = new EtaTraverserImpl(new StubTermTraverser())

  test("traverse()") {
    etaTraverser.traverse(Eta(Term.Name("myMethod")))

    outputWriter.toString shouldBe "this::myMethod"
  }

}
