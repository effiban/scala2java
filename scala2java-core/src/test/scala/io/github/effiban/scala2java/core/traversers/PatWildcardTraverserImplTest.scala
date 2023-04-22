package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite

import scala.meta.Pat

class PatWildcardTraverserImplTest extends UnitTestSuite {

  test("traverse()") {
    PatWildcardTraverser.traverse(Pat.Wildcard()).structure shouldBe Pat.Wildcard().structure
  }
}
