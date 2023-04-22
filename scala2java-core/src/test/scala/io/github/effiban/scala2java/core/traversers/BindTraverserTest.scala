package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite

import scala.meta.XtensionQuasiquoteCaseOrPattern

class BindTraverserTest extends UnitTestSuite {

  test("testTraverse") {
    val bind = p"x @ Int"

    BindTraverser.traverse(bind).structure shouldBe bind.structure
  }

}
