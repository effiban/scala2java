package io.github.effiban.scala2java.core.typeinference

import io.github.effiban.scala2java.core.contexts.TermSelectInferenceContext
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.testtrees.TypeNames
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.{XtensionQuasiquoteTerm, XtensionQuasiquoteType}

class SelectTypeInferrerImplTest extends UnitTestSuite {

  private val qualifierTypeInferrer = mock[QualifierTypeInferrer]
  private val selectWithContextTypeInferrer = mock[SelectWithContextTypeInferrer]

  private val selectTypeInferrer = new SelectTypeInferrerImpl(qualifierTypeInferrer, selectWithContextTypeInferrer)

  test("infer() when inner inferrer returns a type") {
    val termSelect = q"a.b"
    val qualifierType = t"A"
    val expectedSelectType = TypeNames.String

    when(qualifierTypeInferrer.infer(eqTree(termSelect))).thenReturn(Some(qualifierType))
    when(selectWithContextTypeInferrer.infer(termSelect, TermSelectInferenceContext(Some(qualifierType)))).thenReturn(Some(expectedSelectType))

    selectTypeInferrer.infer(termSelect).value.structure shouldBe expectedSelectType.structure
  }

  test("infer() when inner inferrer returns None") {
    val termSelect = q"a.b"
    val qualifierType = t"A"

    when(qualifierTypeInferrer.infer(eqTree(termSelect))).thenReturn(Some(qualifierType))
    when(selectWithContextTypeInferrer.infer(termSelect, TermSelectInferenceContext(Some(qualifierType)))).thenReturn(None)

    selectTypeInferrer.infer(termSelect) shouldBe None
  }
}
