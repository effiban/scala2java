package io.github.effiban.scala2java.core.typeinference

import io.github.effiban.scala2java.core.factories.TermApplyInferenceContextFactory
import io.github.effiban.scala2java.core.matchers.TermApplyInferenceContextMockitoMatcher.eqTermApplyInferenceContext
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.testtrees.TypeNames
import io.github.effiban.scala2java.spi.contexts.TermApplyInferenceContext
import io.github.effiban.scala2java.spi.entities.PartialDeclDef
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.{XtensionQuasiquoteTerm, XtensionQuasiquoteType}

class ApplyReturnTypeInferrerImplTest extends UnitTestSuite {

  private val TheTermApply = q"foo(x)"
  private val ParentType = t"Foo"
  private val MaybeArgTypes = List(Some(TypeNames.String), Some(TypeNames.Int))
  private val InferenceContext = TermApplyInferenceContext(Some(ParentType), MaybeArgTypes)
  private val ReturnType = TypeNames.Long

  private val inferenceContextFactory = mock[TermApplyInferenceContextFactory]
  private val applyDeclDefInferrer = mock[InternalApplyDeclDefInferrer]

  private val applyReturnTypeInferrer = new ApplyReturnTypeInferrerImpl(inferenceContextFactory, applyDeclDefInferrer)

  test("infer() when return type inferred") {
    when(inferenceContextFactory.create(eqTree(TheTermApply))).thenReturn(InferenceContext)
    when(applyDeclDefInferrer.infer(eqTree(TheTermApply), eqTermApplyInferenceContext(InferenceContext)))
      .thenReturn(PartialDeclDef(maybeReturnType = Some(ReturnType)))

    applyReturnTypeInferrer.infer(TheTermApply).value.structure shouldBe ReturnType.structure
  }

  test("infer() when return type not inferred") {
    when(inferenceContextFactory.create(eqTree(TheTermApply))).thenReturn(InferenceContext)
    when(applyDeclDefInferrer.infer(eqTree(TheTermApply), eqTermApplyInferenceContext(InferenceContext))).thenReturn(PartialDeclDef())

    applyReturnTypeInferrer.infer(TheTermApply) shouldBe None
  }
}
