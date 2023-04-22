package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite

import scala.meta.Pat

class PatSeqWildcardTraverserImplTest extends UnitTestSuite {

  test("traverse()") {
    PatSeqWildcardTraverser.traverse(Pat.SeqWildcard()).structure shouldBe Pat.SeqWildcard().structure
  }
}
