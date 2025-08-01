package io.github.effiban.scala2java.core.typeinference

import io.github.effiban.scala2java.core.entities.TypeSelects.ScalaInt
import io.github.effiban.scala2java.core.matchers.PartialDeclDefScalatestMatcher.equalPartialDeclDef
import io.github.effiban.scala2java.core.matchers.TermApplyInferenceContextMockitoMatcher.eqTermApplyInferenceContext
import io.github.effiban.scala2java.core.predicates.TermSelectHasApplyMethod
import io.github.effiban.scala2java.core.reflection.ScalaReflectionMethodSignatureInferrer
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.spi.contexts.TermApplyInferenceContext
import io.github.effiban.scala2java.spi.entities.PartialDeclDef
import io.github.effiban.scala2java.spi.typeinferrers.ApplyDeclDefInferrer
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchersSugar.eqTo

import scala.meta.XtensionQuasiquoteTerm

class InternalApplyDeclDefInferrerImpl_NotByReflectionTest extends UnitTestSuite {

  private val ThePartialDeclDef = PartialDeclDef(maybeReturnType = Some(ScalaInt))

  private val applyDeclDefInferrer = mock[ApplyDeclDefInferrer]
  private val termSelectHasApplyMethod = mock[TermSelectHasApplyMethod]
  private val scalaReflectionMethodSignatureInferrer = mock[ScalaReflectionMethodSignatureInferrer]

  private val internalApplyDeclDefInferrer = new InternalApplyDeclDefInferrerImpl(
    applyDeclDefInferrer,
    termSelectHasApplyMethod,
    scalaReflectionMethodSignatureInferrer
  )

  test("infer() when 'fun' is a Term.Select with an 'apply' method") {
    val termSelect = q"foo.bar"
    val termApply = q"foo.bar(x)"
    val adjustedTermApply = q"foo.bar.apply(x)"
    val context = TermApplyInferenceContext(maybeParentType = Some(ScalaInt))

    when(termSelectHasApplyMethod(eqTree(termSelect))).thenReturn(true)
    when(applyDeclDefInferrer.infer(eqTree(adjustedTermApply), eqTermApplyInferenceContext(context))).thenReturn(ThePartialDeclDef)

    internalApplyDeclDefInferrer.infer(termApply, context) should equalPartialDeclDef(ThePartialDeclDef)
  }

  test("infer() when 'fun' is a Term.ApplyType with a Term.Select having an 'apply' method") {
    val termSelect = q"foo.bar"
    val termApply = q"foo.bar[scala.Int](x)"
    val adjustedTermApply = q"foo.bar.apply[scala.Int](x)"
    val context = TermApplyInferenceContext(maybeParentType = Some(ScalaInt))

    when(termSelectHasApplyMethod(eqTree(termSelect))).thenReturn(true)
    when(applyDeclDefInferrer.infer(eqTree(adjustedTermApply), eqTermApplyInferenceContext(context))).thenReturn(ThePartialDeclDef)

    internalApplyDeclDefInferrer.infer(termApply, context) should equalPartialDeclDef(ThePartialDeclDef)
  }

  test("infer() when 'fun' is a Term.Select with no 'apply' method, and inferred by custom inferrer") {
    val termSelect = q"foo.bar"
    val termApply = q"foo.bar(x)"
    val context = TermApplyInferenceContext(maybeParentType = Some(ScalaInt))

    when(termSelectHasApplyMethod(eqTree(termSelect))).thenReturn(false)
    when(applyDeclDefInferrer.infer(eqTree(termApply), eqTermApplyInferenceContext(context))).thenReturn(ThePartialDeclDef)

    internalApplyDeclDefInferrer.infer(termApply, context) should equalPartialDeclDef(ThePartialDeclDef)
  }

  test("infer() when 'fun' is a Term.ApplyType with a Term.Select, which does not have an 'apply' method, " +
    "and inferred by custom inferrer") {
    val termName = q"foo.bar"
    val termApply = q"foo.bar[scala.Int](x)"
    val context = TermApplyInferenceContext(maybeParentType = Some(ScalaInt))

    when(termSelectHasApplyMethod(eqTree(termName))).thenReturn(false)
    when(applyDeclDefInferrer.infer(eqTree(termApply), eqTermApplyInferenceContext(context))).thenReturn(ThePartialDeclDef)

    internalApplyDeclDefInferrer.infer(termApply, context) should equalPartialDeclDef(ThePartialDeclDef)
  }

  test("infer() when 'fun' is a Term.Apply, and inferred by custom inferrer") {
    val termApply = q"foo(2).bar(3)"
    val context = TermApplyInferenceContext(maybeParentType = Some(ScalaInt))

    when(applyDeclDefInferrer.infer(eqTree(termApply), eqTermApplyInferenceContext(context))).thenReturn(ThePartialDeclDef)

    internalApplyDeclDefInferrer.infer(termApply, context) should equalPartialDeclDef(ThePartialDeclDef)
  }

  test("infer() when 'fun' is a Term.Select with a qualifier that is a Term.Apply," +
    " with no 'apply' method, " +
    "and not inferred by custom inferrer, " +
    "and parent type not inferred") {
    val termSelect = q"foo(5).bar"
    val termApply = q"foo(5).bar(x)"
    val context = TermApplyInferenceContext()

    when(termSelectHasApplyMethod(eqTree(termSelect))).thenReturn(false)
    when(applyDeclDefInferrer.infer(eqTree(termApply), eqTo(context))).thenReturn(PartialDeclDef())

    internalApplyDeclDefInferrer.infer(termApply, context) should equalPartialDeclDef(PartialDeclDef())
  }

  test("infer() when 'fun' is a Term.ApplyType with a Term.Select with no 'apply' method, " +
    "and not inferred by custom inferrer, " +
    "and parent type not inferred") {
    val termSelect = q"foo.bar"
    val termApply = q"foo.bar[scala.Int](x)"
    val context = TermApplyInferenceContext()

    when(termSelectHasApplyMethod(eqTree(termSelect))).thenReturn(false)
    when(applyDeclDefInferrer.infer(eqTree(termApply), eqTo(context))).thenReturn(PartialDeclDef())

    internalApplyDeclDefInferrer.infer(termApply, context) should equalPartialDeclDef(PartialDeclDef())
  }

  test("infer() when 'fun' is a Term.Apply, and not inferred by custom inferrer, " +
    "and parent type not inferred") {
    val termApply = q"foo(2).bar(3)"
    val context = TermApplyInferenceContext()

    when(applyDeclDefInferrer.infer(eqTree(termApply), eqTo(context))).thenReturn(PartialDeclDef())

    internalApplyDeclDefInferrer.infer(termApply, context) should equalPartialDeclDef(PartialDeclDef())
  }
}
