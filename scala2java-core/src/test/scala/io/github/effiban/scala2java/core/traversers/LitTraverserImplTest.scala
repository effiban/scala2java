package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite

import scala.meta.Lit

class LitTraverserImplTest extends UnitTestSuite {

  test("traverse") {
    val lit = Lit.Int(3)

    LitTraverser.traverse(lit).structure shouldBe lit.structure
  }
}
