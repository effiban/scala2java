package io.github.effiban.scala2java.core.typeinference

import io.github.effiban.scala2java.core.matchers.PartialDeclDefScalatestMatcher.equalPartialDeclDef
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.testtrees.TypeNames
import io.github.effiban.scala2java.spi.contexts.TermApplyInferenceContext
import io.github.effiban.scala2java.spi.entities.PartialDeclDef
import io.github.effiban.scala2java.spi.predicates.TermSelectHasApplyMethod
import io.github.effiban.scala2java.spi.typeinferrers.ApplyDeclDefInferrer
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchersSugar.eqTo

import scala.meta.XtensionQuasiquoteTerm

class InternalApplyDeclDefInferrerImplTest extends UnitTestSuite {

  private val ThePartialDeclDef = PartialDeclDef(maybeReturnType = Some(TypeNames.Int))

  private val applyDeclDefInferrer = mock[ApplyDeclDefInferrer]
  private val termSelectHasApplyMethod = mock[TermSelectHasApplyMethod]

  private val context = mock[TermApplyInferenceContext]

  private val internalApplyDeclDefInferrer = new InternalApplyDeclDefInferrerImpl(
    applyDeclDefInferrer,
    termSelectHasApplyMethod
  )

  test("infer() when 'fun' is a Term.Select with an 'apply' method") {
    val termSelect = q"foo.bar"
    val termApply = q"foo.bar(x)"
    val adjustedTermApply = q"foo.bar.apply(x)"

    when(termSelectHasApplyMethod(eqTree(termSelect))).thenReturn(true)
    when(applyDeclDefInferrer.infer(eqTree(adjustedTermApply), eqTo(context))).thenReturn(ThePartialDeclDef)

    internalApplyDeclDefInferrer.infer(termApply, context) should equalPartialDeclDef(ThePartialDeclDef)
  }

  test("infer() when 'fun' is a Term.ApplyType with a Term.Select having an 'apply' method") {
    val termSelect = q"foo.bar"
    val termApply = q"foo.bar[scala.Int](x)"
    val adjustedTermApply = q"foo.bar.apply[scala.Int](x)"

    when(termSelectHasApplyMethod(eqTree(termSelect))).thenReturn(true)
    when(applyDeclDefInferrer.infer(eqTree(adjustedTermApply), eqTo(context))).thenReturn(ThePartialDeclDef)

    internalApplyDeclDefInferrer.infer(termApply, context) should equalPartialDeclDef(ThePartialDeclDef)
  }

  test("infer() when 'fun' is a Term.Select with no 'apply' method") {
    val termSelect = q"foo.bar"
    val termApply = q"foo.bar(x)"

    when(termSelectHasApplyMethod(eqTree(termSelect))).thenReturn(false)
    when(applyDeclDefInferrer.infer(eqTree(termApply), eqTo(context))).thenReturn(ThePartialDeclDef)

    internalApplyDeclDefInferrer.infer(termApply, context) should equalPartialDeclDef(ThePartialDeclDef)
  }

  test("infer() when 'fun' is a Term.ApplyType with a Term.Select, which does not have an 'apply' method") {
    val termName = q"foo.bar"
    val termApply = q"foo.bar[scala.Int](x)"

    when(termSelectHasApplyMethod(eqTree(termName))).thenReturn(false)
    when(applyDeclDefInferrer.infer(eqTree(termApply), eqTo(context))).thenReturn(ThePartialDeclDef)

    internalApplyDeclDefInferrer.infer(termApply, context) should equalPartialDeclDef(ThePartialDeclDef)
  }


  test("infer() when 'fun' is a Term.Apply") {
    val termApply = q"foo(2).bar(3)"

    when(applyDeclDefInferrer.infer(eqTree(termApply), eqTo(context))).thenReturn(ThePartialDeclDef)

    internalApplyDeclDefInferrer.infer(termApply, context) should equalPartialDeclDef(ThePartialDeclDef)
  }
}
