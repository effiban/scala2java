package io.github.effiban.scala2java.core.desugarers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.XtensionQuasiquoteTerm

class ApplyUnaryDesugarerImplTest extends UnitTestSuite {

  private val evaluatedTermDesugarer = mock[EvaluatedTermDesugarer]

  val applyUnaryDesugarer = new ApplyUnaryDesugarerImpl(evaluatedTermDesugarer)

  test("desugar Term.ApplyUnary") {
    val termName = q"func"
    val termApply = q"func()"

    val applyUnary = q"!func"
    val desugaredApplyUnary = q"!func()"

    doReturn(termApply).when(evaluatedTermDesugarer).desugar(eqTree(termName))

    applyUnaryDesugarer.desugar(applyUnary).structure shouldBe desugaredApplyUnary.structure
  }
}
