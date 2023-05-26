package io.github.effiban.scala2java.core.desugarers

import io.github.effiban.scala2java.core.matchers.TermSelectInferenceContextMatcher.eqTermSelectInferenceContext
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.typeinference.QualifierTypeInferrer
import io.github.effiban.scala2java.spi.contexts.TermSelectInferenceContext
import io.github.effiban.scala2java.spi.predicates.TermSelectSupportsNoArgInvocation
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.{XtensionQuasiquoteTerm, XtensionQuasiquoteType}

class EvaluatedTermSelectDesugarerImplTest extends UnitTestSuite {

  private val qualifierTypeInferrer = mock[QualifierTypeInferrer]
  private val termSelectSupportsNoArgInvocation = mock[TermSelectSupportsNoArgInvocation]
  private val evaluatedTermSelectQualDesugarer = mock[EvaluatedTermSelectQualDesugarer]

  private val evaluatedTermSelectDesugarer = new EvaluatedTermSelectDesugarerImpl(
    qualifierTypeInferrer,
    termSelectSupportsNoArgInvocation,
    evaluatedTermSelectQualDesugarer)

  test("desugar when supports no-arg invocation - should return a corresponding Term.Apply") {
    val qualType = t"A"
    val context = TermSelectInferenceContext(Some(qualType))
    val termSelect = q"a.func"
    val termApply = q"a.func()"

    when(qualifierTypeInferrer.infer(eqTree(termSelect))).thenReturn(Some(qualType))
    when(termSelectSupportsNoArgInvocation(eqTree(termSelect), eqTermSelectInferenceContext(context))).thenReturn(true)

    evaluatedTermSelectDesugarer.desugar(termSelect).structure shouldBe termApply.structure

  }

  test("desugar when does not support no-arg invocation - should return the desugared qualifier with name unchanged") {
    val qualType = t"A"
    val context = TermSelectInferenceContext(Some(qualType))
    val termSelect = q"(func).x"
    val desugaredTermSelect = q"(func()).x"

    when(qualifierTypeInferrer.infer(eqTree(termSelect))).thenReturn(Some(qualType))
    when(termSelectSupportsNoArgInvocation(eqTree(termSelect), eqTermSelectInferenceContext(context))).thenReturn(false)
    doReturn(desugaredTermSelect).when(evaluatedTermSelectQualDesugarer).desugar(eqTree(termSelect))

    evaluatedTermSelectDesugarer.desugar(termSelect).structure shouldBe desugaredTermSelect.structure

  }
}
