package io.github.effiban.scala2java.core.desugarers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.{XtensionQuasiquoteInit, XtensionQuasiquoteTerm}

class EvaluatedTermDesugarerImplTest extends UnitTestSuite {

  private val treeDesugarer = mock[TreeDesugarer]

  private val evaluatedTermDesugarer = new EvaluatedTermDesugarerImpl(treeDesugarer)

  test("desugar 'new'") {
    val `new` = q"new A(func)"
    val desugaredNew = q"new A(func())"

    val init = init"A(func)"
    val desugaredInit = init"A(func())"

    doReturn(desugaredInit).when(treeDesugarer).desugar(eqTree(init))

    evaluatedTermDesugarer.desugar(`new`).structure shouldBe desugaredNew.structure
  }

  test("desugar 'return'") {
    val `return` = q"return x"

    evaluatedTermDesugarer.desugar(`return`).structure shouldBe `return`.structure
  }
}
