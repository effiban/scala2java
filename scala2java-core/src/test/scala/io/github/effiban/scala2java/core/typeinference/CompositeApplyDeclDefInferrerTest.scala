package io.github.effiban.scala2java.core.typeinference

import io.github.effiban.scala2java.core.extensions.ExtensionRegistry
import io.github.effiban.scala2java.core.matchers.PartialDeclDefScalatestMatcher.equalPartialDeclDef
import io.github.effiban.scala2java.core.matchers.TermApplyInferenceContextMockitoMatcher.eqTermApplyInferenceContext
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.testtrees.TypeNames
import io.github.effiban.scala2java.spi.contexts.TermApplyInferenceContext
import io.github.effiban.scala2java.spi.entities.PartialDeclDef
import io.github.effiban.scala2java.spi.typeinferrers.ApplyDeclDefInferrer
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.{XtensionQuasiquoteTerm, XtensionQuasiquoteType}

class CompositeApplyDeclDefInferrerTest extends UnitTestSuite {

  private val TheTermApply = q"foo(x, y)"
  private val MaybeArgTypes = List(Some(TypeNames.String), Some(TypeNames.Int))
  private val Context = TermApplyInferenceContext(Some(t"Foo"), MaybeArgTypes)
  
  private val ThePartialDeclDef = PartialDeclDef(
    maybeParamNames = List(Some(q"param1"), Some(q"param2")),
    maybeParamTypes = List(Some(TypeNames.String), Some(TypeNames.Int)),
    maybeReturnType = Some(TypeNames.Long)
  )

  private implicit val extensionRegistry: ExtensionRegistry = mock[ExtensionRegistry]

  private val coreInferrer = mock[ApplyDeclDefInferrer]
  private val otherInferrer1 = mock[ApplyDeclDefInferrer]
  private val otherInferrer2 = mock[ApplyDeclDefInferrer]

  test("infer when there are no extension inferrers and core inferrer returns empty - should return empty") {
    when(coreInferrer.infer(eqTree(TheTermApply), eqTermApplyInferenceContext(Context))).thenReturn(PartialDeclDef())

    compositeInferrer().infer(TheTermApply, Context) shouldBe PartialDeclDef()
  }

  test("infer when there are no extension inferrers and core inferrer returns a PartialDeclDef - should return it") {
    when(coreInferrer.infer(eqTree(TheTermApply), eqTermApplyInferenceContext(Context))).thenReturn(ThePartialDeclDef)

    compositeInferrer().infer(TheTermApply, Context) should equalPartialDeclDef(ThePartialDeclDef)
  }

  test("infer when there is one extension inferrer returning empty, and core returns a PartialDeclDef - should return it") {
    when(otherInferrer1.infer(eqTree(TheTermApply), eqTermApplyInferenceContext(Context))).thenReturn(PartialDeclDef())
    when(coreInferrer.infer(eqTree(TheTermApply), eqTermApplyInferenceContext(Context))).thenReturn(ThePartialDeclDef)

    compositeInferrer(List(otherInferrer1)).infer(TheTermApply, Context) should equalPartialDeclDef(ThePartialDeclDef)
  }

  test("infer when there is one extension inferrer returning a PartialDeclDef - should return it") {
    when(otherInferrer1.infer(eqTree(TheTermApply), eqTermApplyInferenceContext(Context))).thenReturn(ThePartialDeclDef)

    compositeInferrer(List(otherInferrer1)).infer(TheTermApply, Context) should equalPartialDeclDef(ThePartialDeclDef)
  }

  test("infer when there is one extension inferrer returning a PartialDeclDef - should not invoke the core inferrer") {
    when(otherInferrer1.infer(eqTree(TheTermApply), eqTermApplyInferenceContext(Context))).thenReturn(ThePartialDeclDef)

    compositeInferrer(List(otherInferrer1)).infer(TheTermApply, Context)

    verifyNoMoreInteractions(coreInferrer)
  }

  test("infer when there are two extension inferrers, both returning empty, and the core also returns empty - should return empty") {
    when(otherInferrer1.infer(eqTree(TheTermApply), eqTermApplyInferenceContext(Context))).thenReturn(PartialDeclDef())
    when(otherInferrer2.infer(eqTree(TheTermApply), eqTermApplyInferenceContext(Context))).thenReturn(PartialDeclDef())
    when(coreInferrer.infer(eqTree(TheTermApply), eqTermApplyInferenceContext(Context))).thenReturn(PartialDeclDef())

    compositeInferrer(List(otherInferrer1, otherInferrer2)).infer(TheTermApply, Context) shouldBe PartialDeclDef()
  }

  test("infer when there are two extension inferrers, both returning empty, and the core returns a PartialDeclDef - should return empty") {
    when(otherInferrer1.infer(eqTree(TheTermApply), eqTermApplyInferenceContext(Context))).thenReturn(PartialDeclDef())
    when(otherInferrer2.infer(eqTree(TheTermApply), eqTermApplyInferenceContext(Context))).thenReturn(PartialDeclDef())
    when(coreInferrer.infer(eqTree(TheTermApply), eqTermApplyInferenceContext(Context))).thenReturn(ThePartialDeclDef)

    compositeInferrer(List(otherInferrer1, otherInferrer2)).infer(TheTermApply, Context) should equalPartialDeclDef(ThePartialDeclDef)
  }

  test("infer when there are two extension inferrers, first returns empty and second returns a PartialDeclDef - should return it") {
    when(otherInferrer1.infer(eqTree(TheTermApply), eqTermApplyInferenceContext(Context))).thenReturn(PartialDeclDef())
    when(otherInferrer2.infer(eqTree(TheTermApply), eqTermApplyInferenceContext(Context))).thenReturn(ThePartialDeclDef)

    compositeInferrer(List(otherInferrer1, otherInferrer2)).infer(TheTermApply, Context) should equalPartialDeclDef(ThePartialDeclDef)
  }

  test("infer when there are two extension inferrers, first returns empty and second returns a PartialDeclDef - should not invoke the core inferrer") {
    when(otherInferrer1.infer(eqTree(TheTermApply), eqTermApplyInferenceContext(Context))).thenReturn(PartialDeclDef())
    when(otherInferrer2.infer(eqTree(TheTermApply), eqTermApplyInferenceContext(Context))).thenReturn(ThePartialDeclDef)

    compositeInferrer(List(otherInferrer1, otherInferrer2)).infer(TheTermApply, Context)

    verifyNoMoreInteractions(coreInferrer)
  }

  test("infer when there are two extension inferrers and first returns a PartialDeclDef - should return it") {
    when(otherInferrer1.infer(eqTree(TheTermApply), eqTermApplyInferenceContext(Context))).thenReturn(ThePartialDeclDef)

    compositeInferrer(List(otherInferrer1, otherInferrer2)).infer(TheTermApply, Context) should equalPartialDeclDef(ThePartialDeclDef)
  }

  test("infer when there are two extension inferrers and first returns a PartialDeclDef - should not invoke any extension inferrer") {
    when(otherInferrer1.infer(eqTree(TheTermApply), eqTermApplyInferenceContext(Context))).thenReturn(ThePartialDeclDef)

    compositeInferrer(List(otherInferrer1, otherInferrer2)).infer(TheTermApply, Context)

    verifyNoMoreInteractions(otherInferrer2, coreInferrer)
  }

  private def compositeInferrer(extensionInferrers: List[ApplyDeclDefInferrer] = Nil) = {
    when(extensionRegistry.applyDeclDefInferrers).thenReturn(extensionInferrers)
    new CompositeApplyDeclDefInferrer(coreInferrer)
  }
}
