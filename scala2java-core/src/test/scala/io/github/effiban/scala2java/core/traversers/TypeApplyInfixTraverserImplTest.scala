package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite

import scala.meta.XtensionQuasiquoteType

class TypeApplyInfixTraverserImplTest extends UnitTestSuite {
  private val typeApplyInfixTraverser = new TypeApplyInfixTraverserImpl()

  test("traverse") {
    val typeApplyInfix = t"K Map V"

    typeApplyInfixTraverser.traverse(typeApplyInfix).structure shouldBe typeApplyInfix.structure
  }
}
