package io.github.effiban.scala2java.core.desugarers.semantic

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.XtensionQuasiquoteTerm

class EvaluatedTermSelectQualDesugarerImplTest extends UnitTestSuite {

  private val evaluatedTermDesugarer = mock[EvaluatedTermDesugarer]
  private val evaluatedTermSelectQualDesugarer = new EvaluatedTermSelectQualDesugarerImpl(evaluatedTermDesugarer)

  test("desugar") {
    val qual = q"func"
    val desugaredQual = q"func()"

    val termSelect = q"(func).x"
    val desugaredTermSelect = q"(func()).x"

    doReturn(desugaredQual).when(evaluatedTermDesugarer).desugar(eqTree(qual))

    evaluatedTermSelectQualDesugarer.desugar(termSelect).structure shouldBe desugaredTermSelect.structure

  }

}
