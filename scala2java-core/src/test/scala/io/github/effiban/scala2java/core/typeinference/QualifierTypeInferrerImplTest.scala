package io.github.effiban.scala2java.core.typeinference

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.testtrees.TypeNames
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.XtensionQuasiquoteTerm

class QualifierTypeInferrerImplTest extends UnitTestSuite {

  private val termTypeInferrer = mock[TermTypeInferrer]

  private val qualifierTypeInferrer = new QualifierTypeInferrerImpl(termTypeInferrer)

  test("infer() when inner inferrer returns a type") {
    val termSelect = q"aa.bb"

    when(termTypeInferrer.infer(eqTree(q"aa"))).thenReturn(Some(TypeNames.Int))

    qualifierTypeInferrer.infer(termSelect).value.structure shouldBe TypeNames.Int.structure
  }

  test("infer() when inner inferrer returns None") {
    val termSelect = q"aa.bb"

    when(termTypeInferrer.infer(eqTree(q"aa"))).thenReturn(None)

    qualifierTypeInferrer.infer(termSelect) shouldBe None
  }
}
