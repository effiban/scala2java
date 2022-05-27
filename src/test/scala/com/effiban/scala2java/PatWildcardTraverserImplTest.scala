package com.effiban.scala2java

import scala.meta.Pat

class PatWildcardTraverserImplTest extends UnitTestSuite {

  val patWildcardTraverser = new PatWildcardTraverserImpl()

  test("traverse()") {
    patWildcardTraverser.traverse(Pat.Wildcard())

    outputWriter.toString shouldBe "default"
  }
}
