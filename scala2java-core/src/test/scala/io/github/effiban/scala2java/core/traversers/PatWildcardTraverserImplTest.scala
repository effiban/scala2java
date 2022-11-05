package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite

import scala.meta.Pat

class PatWildcardTraverserImplTest extends UnitTestSuite {

  val patWildcardTraverser = new PatWildcardTraverserImpl()

  test("traverse()") {
    patWildcardTraverser.traverse(Pat.Wildcard())

    outputWriter.toString shouldBe "__"
  }
}
