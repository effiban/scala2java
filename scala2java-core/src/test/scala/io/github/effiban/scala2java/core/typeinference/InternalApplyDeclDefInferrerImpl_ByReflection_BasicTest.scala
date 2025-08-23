package io.github.effiban.scala2java.core.typeinference

import io.github.effiban.scala2java.core.entities.TypeSelects.{ScalaAny, ScalaInt}
import io.github.effiban.scala2java.core.factories.TermApplyInferenceContextFactory
import io.github.effiban.scala2java.core.matchers.PartialDeclDefScalatestMatcher.equalPartialDeclDef
import io.github.effiban.scala2java.core.matchers.TermApplyInferenceContextMockitoMatcher.eqTermApplyInferenceContext
import io.github.effiban.scala2java.core.predicates.TermSelectHasApplyMethod
import io.github.effiban.scala2java.core.reflection.ScalaReflectionMethodSignatureInferrer
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.spi.contexts.TermApplyInferenceContext
import io.github.effiban.scala2java.spi.entities.PartialDeclDef
import io.github.effiban.scala2java.spi.typeinferrers.ApplyDeclDefInferrer
import io.github.effiban.scala2java.test.utils.matchers.CombinedMatchers.eqTreeMultiList
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.{XtensionQuasiquoteTerm, XtensionQuasiquoteType}

class InternalApplyDeclDefInferrerImpl_ByReflection_BasicTest extends UnitTestSuite {

  private val ParentType = t"Parent"

  private val applyDeclDefInferrer = mock[ApplyDeclDefInferrer]
  private val termSelectHasApplyMethod = mock[TermSelectHasApplyMethod]
  private val scalaReflectionMethodSignatureInferrer = mock[ScalaReflectionMethodSignatureInferrer]
  private val termApplyInferenceContextFactory = mock[TermApplyInferenceContextFactory]

  private val internalApplyDeclDefInferrer = new InternalApplyDeclDefInferrerImpl(
    applyDeclDefInferrer,
    termSelectHasApplyMethod,
    scalaReflectionMethodSignatureInferrer,
    termApplyInferenceContextFactory
  )

  test("infer() when parent type inferred, " +
    "and arg types inferred, " +
    "and inferrer returns empty") {
    val termSelect = q"foo.bar"
    val termApply = q"foo.bar(x)"
    val argType = t"scala.Int"
    val context = TermApplyInferenceContext(
      maybeParentType = Some(ParentType),
      maybeArgTypeLists = List(List(Some(argType)))
    )

    when(termSelectHasApplyMethod(eqTree(termSelect))).thenReturn(false)
    when(applyDeclDefInferrer.infer(eqTree(termApply), eqTermApplyInferenceContext(context))).thenReturn(PartialDeclDef())
    when(scalaReflectionMethodSignatureInferrer.inferPartialMethodSignature(
      eqTree(ParentType),
      eqTree(q"bar"),
      eqTreeMultiList(List(List(argType)))
    )).thenReturn(PartialDeclDef())

    internalApplyDeclDefInferrer.infer(termApply, context) should equalPartialDeclDef(PartialDeclDef())
  }

  test("infer() when parent type inferred, " +
    "and arg types inferred, " +
    "and inferrer returns a partial signature") {
    val termSelect = q"foo.bar"
    val termApply = q"foo.bar(x)"
    val argType = t"scala.Int"
    val context = TermApplyInferenceContext(
      maybeParentType = Some(ParentType),
      maybeArgTypeLists = List(List(Some(argType)))
    )
    val expectedPartialDeclDef = PartialDeclDef(maybeReturnType = Some(ScalaInt))

    when(termSelectHasApplyMethod(eqTree(termSelect))).thenReturn(false)
    when(applyDeclDefInferrer.infer(eqTree(termApply), eqTermApplyInferenceContext(context))).thenReturn(PartialDeclDef())
    when(scalaReflectionMethodSignatureInferrer.inferPartialMethodSignature(
      eqTree(ParentType),
      eqTree(q"bar"),
      eqTreeMultiList(List(List(argType)))
    )).thenReturn(expectedPartialDeclDef)

    internalApplyDeclDefInferrer.infer(termApply, context) should equalPartialDeclDef(expectedPartialDeclDef)
  }

  test("infer() when parent type inferred, " +
    "and arg types not inferred, " +
    "and inferrer returns a partial signature") {
    val termSelect = q"foo.bar"
    val termApply = q"foo.bar(x)"
    val context = TermApplyInferenceContext(
      maybeParentType = Some(ParentType),
      maybeArgTypeLists = List(List(None))
    )

    when(termSelectHasApplyMethod(eqTree(termSelect))).thenReturn(false)
    when(applyDeclDefInferrer.infer(eqTree(termApply), eqTermApplyInferenceContext(context))).thenReturn(PartialDeclDef())
    when(scalaReflectionMethodSignatureInferrer.inferPartialMethodSignature(
      eqTree(ParentType),
      eqTree(q"bar"),
      eqTreeMultiList(List(List(ScalaAny)))
    )).thenReturn(PartialDeclDef())

    internalApplyDeclDefInferrer.infer(termApply, context) should equalPartialDeclDef(PartialDeclDef())
  }

  test("infer() when inner qual is a Term.Ref, " +
    "and parent type not inferred, " +
    "and arg types inferred, " +
    "and inferrer returns empty") {
    val termSelect = q"foo.bar"
    val termApply = q"foo.bar(x)"
    val qual = q"foo"
    val argType = t"scala.Int"
    val context = TermApplyInferenceContext(
      maybeArgTypeLists = List(List(Some(argType)))
    )

    when(termSelectHasApplyMethod(eqTree(termSelect))).thenReturn(false)
    when(applyDeclDefInferrer.infer(eqTree(termApply), eqTermApplyInferenceContext(context))).thenReturn(PartialDeclDef())
    when(scalaReflectionMethodSignatureInferrer.inferPartialMethodSignature(
      eqTree(qual),
      eqTree(termSelect.name),
      eqTreeMultiList(List(List(argType)))
    )).thenReturn(PartialDeclDef())

    internalApplyDeclDefInferrer.infer(termApply, context) should equalPartialDeclDef(PartialDeclDef())
  }

  test("infer() when inner qual is a Term.Ref, " +
    "and parent type not inferred, " +
    "and arg types inferred, " +
    "and inferrer returns a partial signature") {
    val termSelect = q"foo.bar"
    val termApply = q"foo.bar(x)"
    val qual = q"foo"
    val argType = t"scala.Int"
    val context = TermApplyInferenceContext(
      maybeArgTypeLists = List(List(Some(argType)))
    )
    val expectedPartialDeclDef = PartialDeclDef(maybeReturnType = Some(ScalaInt))

    when(termSelectHasApplyMethod(eqTree(termSelect))).thenReturn(false)
    when(applyDeclDefInferrer.infer(eqTree(termApply), eqTermApplyInferenceContext(context))).thenReturn(expectedPartialDeclDef)
    when(scalaReflectionMethodSignatureInferrer.inferPartialMethodSignature(
      eqTree(qual),
      eqTree(termSelect.name),
      eqTreeMultiList(List(List(argType)))
    )).thenReturn(PartialDeclDef())

    internalApplyDeclDefInferrer.infer(termApply, context) should equalPartialDeclDef(expectedPartialDeclDef)
  }
}
