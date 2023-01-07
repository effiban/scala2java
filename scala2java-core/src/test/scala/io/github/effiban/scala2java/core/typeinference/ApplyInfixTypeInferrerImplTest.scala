package io.github.effiban.scala2java.core.typeinference

import io.github.effiban.scala2java.core.classifiers.TermApplyInfixClassifier
import io.github.effiban.scala2java.core.entities.TermApplyInfixKind.{Association, Unclassified}
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.{XtensionQuasiquoteTerm, XtensionQuasiquoteType}

class ApplyInfixTypeInferrerImplTest extends UnitTestSuite {

  private val tupleTypeInferrer = mock[TupleTypeInferrer]
  private val termApplyInfixClassifier = mock[TermApplyInfixClassifier]

  private val applyInfixTypeInferrer = new ApplyInfixTypeInferrerImpl(tupleTypeInferrer, termApplyInfixClassifier)

  test("infer when infix is association") {
    val applyInfix = q"a -> 1"
    val expectedTermTuple = q"(a, 1)"
    val expectedTypeTuple = t"(String, Int)"

    when(termApplyInfixClassifier.classify(eqTree(applyInfix))).thenReturn(Association)
    when(tupleTypeInferrer.infer(eqTree(expectedTermTuple))).thenReturn(expectedTypeTuple)

    applyInfixTypeInferrer.infer(applyInfix).value.structure shouldBe expectedTypeTuple.structure
  }

  test("infer when infix is regular method") {
    val applyInfix = q"a myMethod b"

    when(termApplyInfixClassifier.classify(eqTree(applyInfix))).thenReturn(Unclassified)

    applyInfixTypeInferrer.infer(applyInfix) shouldBe None
  }
}
