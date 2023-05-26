package io.github.effiban.scala2java.core.desugarers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.XtensionQuasiquoteTerm

class EvaluatedTermRefDesugarerImplTest extends UnitTestSuite {

  private val evaluatedTermNameDesugarer = mock[EvaluatedTermNameDesugarer]
  private val treeDesugarer = mock[TreeDesugarer]

  private val evaluatedTermRefDesugarer = new EvaluatedTermRefDesugarerImpl(evaluatedTermNameDesugarer, treeDesugarer)

  test("desugar Term.Name") {
    val termName = q"func"
    val termApply = q"func()"

    doReturn(termApply).when(evaluatedTermNameDesugarer).desugar(eqTree(termName))

    evaluatedTermRefDesugarer.desugar(termName).structure shouldBe termApply.structure

  }
}
