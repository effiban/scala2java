package io.github.effiban.scala2java.core.factories

import io.github.effiban.scala2java.core.entities.TypeSelects
import io.github.effiban.scala2java.core.matchers.TermApplyInferenceContextMockitoMatcher.eqTermApplyInferenceContext
import io.github.effiban.scala2java.core.matchers.TermApplyTransformationContextScalatestMatcher.equalTermApplyTransformationContext
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.typeinference.InternalApplyDeclDefInferrer
import io.github.effiban.scala2java.spi.contexts.{TermApplyInferenceContext, TermApplyTransformationContext}
import io.github.effiban.scala2java.spi.entities.PartialDeclDef
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.{Term, XtensionQuasiquoteTerm}

class TermApplyTransformationContextFactoryImplTest extends UnitTestSuite {

  private val termApplyInferenceContextFactory = mock[TermApplyInferenceContextFactory]
  private val applyDeclDefInferrer = mock[InternalApplyDeclDefInferrer]

  private val termApplyTransformationContextFactory = new TermApplyTransformationContextFactoryImpl(
    termApplyInferenceContextFactory,
    applyDeclDefInferrer
  )

  test("create()") {
    val termApply = Term.Apply(q"foo", List(q"2", q"3"))

    val expectedFunType = TypeSelects.ScalaInt
    val expectedMaybeArgTypes = List(Some(TypeSelects.ScalaLong), Some(TypeSelects.ScalaString))

    val expectedPartialDeclDef = PartialDeclDef(
      maybeParamNames = List(Some(q"param1"), Some(q"param2")),
      maybeParamTypes = expectedMaybeArgTypes,
      maybeReturnType = Some(expectedFunType)
    )

    val expectedPartialDeclDefContext = TermApplyInferenceContext(
      maybeParentType = Some(expectedFunType),
      maybeArgTypes = expectedMaybeArgTypes
    )

    val expectedTransformationContext = TermApplyTransformationContext(
      maybeQualifierType = Some(expectedFunType),
      partialDeclDef = expectedPartialDeclDef
    )

    when(termApplyInferenceContextFactory.create(termApply)).thenReturn(expectedPartialDeclDefContext)
    when(applyDeclDefInferrer.infer(eqTree(termApply), eqTermApplyInferenceContext(expectedPartialDeclDefContext)))
      .thenReturn(expectedPartialDeclDef)

    termApplyTransformationContextFactory.create(termApply) should equalTermApplyTransformationContext(expectedTransformationContext)
  }
}
