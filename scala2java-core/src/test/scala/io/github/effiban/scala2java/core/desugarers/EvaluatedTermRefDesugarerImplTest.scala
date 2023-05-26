package io.github.effiban.scala2java.core.desugarers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.XtensionQuasiquoteTerm

class EvaluatedTermRefDesugarerImplTest extends UnitTestSuite {

  private val evaluatedTermNameDesugarer = mock[EvaluatedTermNameDesugarer]
  private val evaluatedTermSelectDesugarer = mock[EvaluatedTermSelectDesugarer]
  private val applyUnaryDesugarer: ApplyUnaryDesugarer = mock[ApplyUnaryDesugarer]
  private val treeDesugarer = mock[TreeDesugarer]

  private val evaluatedTermRefDesugarer = new EvaluatedTermRefDesugarerImpl(
    evaluatedTermNameDesugarer,
    evaluatedTermSelectDesugarer,
    applyUnaryDesugarer,
    treeDesugarer)


  test("desugar Term.Name") {
    val termName = q"func"
    val termApply = q"func()"

    doReturn(termApply).when(evaluatedTermNameDesugarer).desugar(eqTree(termName))

    evaluatedTermRefDesugarer.desugar(termName).structure shouldBe termApply.structure

  }

  test("desugar Term.Select") {
    val termSelect = q"a.func"
    val termApply = q"a.func()"

    doReturn(termApply).when(evaluatedTermSelectDesugarer).desugar(eqTree(termSelect))

    evaluatedTermRefDesugarer.desugar(termSelect).structure shouldBe termApply.structure

  }

  test("desugar Term.ApplyUnary") {
    val applyUnary = q"!func"
    val desugaredApplyUnary = q"!func()"

    doReturn(desugaredApplyUnary).when(applyUnaryDesugarer).desugar(eqTree(applyUnary))

    evaluatedTermRefDesugarer.desugar(applyUnary).structure shouldBe desugaredApplyUnary.structure
  }
}
