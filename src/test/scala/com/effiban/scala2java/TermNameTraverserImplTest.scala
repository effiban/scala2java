package com.effiban.scala2java

import scala.meta.Term

class TermNameTraverserImplTest extends UnitTestSuite {

  private val termNameTraverser = new TermNameTraverserImpl()

  test("traverse") {
    termNameTraverser.traverse(Term.Name("xyz"))

    outputWriter.toString shouldBe "xyz"
  }

}
