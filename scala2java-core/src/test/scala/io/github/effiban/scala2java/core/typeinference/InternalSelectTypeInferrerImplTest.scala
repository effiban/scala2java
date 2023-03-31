package io.github.effiban.scala2java.core.typeinference

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.testtrees.TypeNames
import io.github.effiban.scala2java.spi.contexts.TermSelectInferenceContext
import io.github.effiban.scala2java.spi.predicates.TermSelectSupportsNoArgInvocation
import io.github.effiban.scala2java.spi.typeinferrers.SelectTypeInferrer
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.{XtensionQuasiquoteTerm, XtensionQuasiquoteType}

class InternalSelectTypeInferrerImplTest extends UnitTestSuite {

  private val applyReturnTypeInferrer = mock[ApplyReturnTypeInferrer]
  private val qualifierTypeInferrer = mock[QualifierTypeInferrer]
  private val selectTypeInferrer = mock[SelectTypeInferrer]
  private val termSelectSupportsNoArgInvocation = mock[TermSelectSupportsNoArgInvocation]
  private val internalSelectTypeInferrer = new InternalSelectTypeInferrerImpl(
    applyReturnTypeInferrer,
    qualifierTypeInferrer,
    selectTypeInferrer,
    termSelectSupportsNoArgInvocation
  )

  test("infer() when Term.Select supports a no-arg invocation, should infer as a no-arg Term.Apply and return that result") {
    val termSelect = q"a.b"
    val expectedTermApply = q"a.b()"
    val expectedReturnType = TypeNames.String

    when(termSelectSupportsNoArgInvocation(termSelect)).thenReturn(true)
    when(applyReturnTypeInferrer.infer(eqTree(expectedTermApply))).thenReturn(Some(expectedReturnType))

    internalSelectTypeInferrer.infer(termSelect).value.structure shouldBe expectedReturnType.structure
  }

  test("infer() when Term.Select supports a no-arg invocation, should infer as a no-arg Term.Apply and return None if that returns None") {
    val termSelect = q"a.b"
    val expectedTermApply = q"a.b()"

    when(termSelectSupportsNoArgInvocation(termSelect)).thenReturn(true)
    when(applyReturnTypeInferrer.infer(eqTree(expectedTermApply))).thenReturn(None)

    internalSelectTypeInferrer.infer(termSelect) shouldBe None
  }

  test("infer() when Term.Select does not support no-arg invocation, and inner inferrer returns a type - should return it") {
    val termSelect = q"a.b"
    val qualifierType = t"A"
    val expectedSelectType = TypeNames.String

    when(termSelectSupportsNoArgInvocation(termSelect)).thenReturn(false)
    when(qualifierTypeInferrer.infer(eqTree(termSelect))).thenReturn(Some(qualifierType))
    when(selectTypeInferrer.infer(termSelect, TermSelectInferenceContext(Some(qualifierType)))).thenReturn(Some(expectedSelectType))

    internalSelectTypeInferrer.infer(termSelect).value.structure shouldBe expectedSelectType.structure
  }

  test("infer() when Term.Select does not support no-arg invocation, and inner inferrer returns None - should return None") {
    val termSelect = q"a.b"
    val qualifierType = t"A"

    when(termSelectSupportsNoArgInvocation(termSelect)).thenReturn(false)
    when(qualifierTypeInferrer.infer(eqTree(termSelect))).thenReturn(Some(qualifierType))
    when(selectTypeInferrer.infer(termSelect, TermSelectInferenceContext(Some(qualifierType)))).thenReturn(None)

    internalSelectTypeInferrer.infer(termSelect) shouldBe None
  }
}
