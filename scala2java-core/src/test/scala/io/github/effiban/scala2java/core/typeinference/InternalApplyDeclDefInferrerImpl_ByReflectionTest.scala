package io.github.effiban.scala2java.core.typeinference

import io.github.effiban.scala2java.core.entities.TypeSelects.ScalaInt
import io.github.effiban.scala2java.core.matchers.PartialDeclDefScalatestMatcher.equalPartialDeclDef
import io.github.effiban.scala2java.core.predicates.TermSelectHasApplyMethod
import io.github.effiban.scala2java.core.reflection.ScalaReflectionMethodSignatureInferrer
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.spi.contexts.TermApplyInferenceContext
import io.github.effiban.scala2java.spi.entities.PartialDeclDef
import io.github.effiban.scala2java.spi.typeinferrers.ApplyDeclDefInferrer
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchersSugar.eqTo

import scala.meta.{XtensionQuasiquoteTerm, XtensionQuasiquoteType}

class InternalApplyDeclDefInferrerImpl_ByReflectionTest extends UnitTestSuite {

  private val ParentType = t"Parent"
  private val ContextWithParentType = TermApplyInferenceContext(maybeParentType = Some(ParentType))

  private val applyDeclDefInferrer = mock[ApplyDeclDefInferrer]
  private val termSelectHasApplyMethod = mock[TermSelectHasApplyMethod]
  private val scalaReflectionMethodSignatureInferrer = mock[ScalaReflectionMethodSignatureInferrer]

  private val internalApplyDeclDefInferrer = new InternalApplyDeclDefInferrerImpl(
    applyDeclDefInferrer,
    termSelectHasApplyMethod,
    scalaReflectionMethodSignatureInferrer
  )

  test("infer() when 'fun' is a Term.Select with no 'apply' method, " +
    "and not inferred by custom inferrer, " +
    "and parent type inferred, " +
    "and not inferred by reflection") {
    val termSelect = q"foo.bar"
    val termApply = q"foo.bar(x)"

    when(termSelectHasApplyMethod(eqTree(termSelect))).thenReturn(false)
    when(applyDeclDefInferrer.infer(eqTree(termApply), eqTo(ContextWithParentType))).thenReturn(PartialDeclDef())
    when(scalaReflectionMethodSignatureInferrer.inferPartialMethodSignature(eqTree(ParentType), eqTree(q"bar"))).thenReturn(PartialDeclDef())

    internalApplyDeclDefInferrer.infer(termApply, ContextWithParentType) should equalPartialDeclDef(PartialDeclDef())
  }

  test("infer() when 'fun' is a Term.Select with no 'apply' method, " +
    "and not inferred by custom inferrer, " +
    "and parent type inferred, " +
    "and inferred by reflection") {
    val termSelect = q"foo.bar"
    val termApply = q"foo.bar(x)"
    val expectedPartialDeclDef = PartialDeclDef(maybeReturnType = Some(ScalaInt))

    when(termSelectHasApplyMethod(eqTree(termSelect))).thenReturn(false)
    when(applyDeclDefInferrer.infer(eqTree(termApply), eqTo(ContextWithParentType))).thenReturn(PartialDeclDef())
    when(scalaReflectionMethodSignatureInferrer.inferPartialMethodSignature(eqTree(ParentType), eqTree(q"bar")))
      .thenReturn(expectedPartialDeclDef)

    internalApplyDeclDefInferrer.infer(termApply, ContextWithParentType) should equalPartialDeclDef(expectedPartialDeclDef)
  }

  test("infer() when 'fun' is a Term.Select with no 'apply' method, " +
    "and not inferred by custom inferrer, " +
    "and parent type not inferred, " +
    "and inner qual is a Term.Ref, " +
    "and not inferred by reflection") {
    val termSelect = q"foo.bar"
    val termApply = q"foo.bar(x)"
    val qual = q"foo"

    when(termSelectHasApplyMethod(eqTree(termSelect))).thenReturn(false)
    when(applyDeclDefInferrer.infer(eqTree(termApply), eqTo(TermApplyInferenceContext()))).thenReturn(PartialDeclDef())
    when(scalaReflectionMethodSignatureInferrer.inferPartialMethodSignature(eqTree(qual), eqTree(termSelect.name))).thenReturn(PartialDeclDef())

    internalApplyDeclDefInferrer.infer(termApply, TermApplyInferenceContext()) should equalPartialDeclDef(PartialDeclDef())
  }

  test("infer() when 'fun' is a Term.Select with no 'apply' method, " +
    "and not inferred by custom inferrer, " +
    "and parent type not inferred, " +
    "and inner qual is a Term.Ref, " +
    "and inferred by reflection") {
    val termSelect = q"foo.bar"
    val termApply = q"foo.bar(x)"
    val qual = q"foo"
    val expectedPartialDeclDef = PartialDeclDef(maybeReturnType = Some(ScalaInt))

    when(termSelectHasApplyMethod(eqTree(termSelect))).thenReturn(false)
    when(applyDeclDefInferrer.infer(eqTree(termApply), eqTo(TermApplyInferenceContext()))).thenReturn(PartialDeclDef())
    when(scalaReflectionMethodSignatureInferrer.inferPartialMethodSignature(eqTree(qual), eqTree(termSelect.name))).thenReturn(expectedPartialDeclDef)

    internalApplyDeclDefInferrer.infer(termApply, TermApplyInferenceContext()) should equalPartialDeclDef(expectedPartialDeclDef)
  }

}
