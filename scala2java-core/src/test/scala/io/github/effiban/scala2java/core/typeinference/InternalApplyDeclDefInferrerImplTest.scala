package io.github.effiban.scala2java.core.typeinference

import io.github.effiban.scala2java.core.matchers.PartialDeclDefScalatestMatcher.equalPartialDeclDef
import io.github.effiban.scala2java.core.predicates.TermNameHasApplyMethod
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.testtrees.TypeNames
import io.github.effiban.scala2java.spi.contexts.TermApplyInferenceContext
import io.github.effiban.scala2java.spi.entities.PartialDeclDef
import io.github.effiban.scala2java.spi.typeinferrers.ApplyDeclDefInferrer
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchersSugar.eqTo

import scala.meta.XtensionQuasiquoteTerm

class InternalApplyDeclDefInferrerImplTest extends UnitTestSuite {

  private val ThePartialDeclDef = PartialDeclDef(maybeReturnType = Some(TypeNames.Int))

  private val applyDeclDefInferrer = mock[ApplyDeclDefInferrer]
  private val termNameHasApplyMethod = mock[TermNameHasApplyMethod]

  private val context = mock[TermApplyInferenceContext]

  private val internalApplyDeclDefInferrer = new InternalApplyDeclDefInferrerImpl(applyDeclDefInferrer, termNameHasApplyMethod)


  test("infer() when 'fun' is a Term.Name with 'apply' method") {
    val termName = q"foo"
    val termApply = q"foo(x)"
    val adjustedTermApply = q"foo.apply(x)"

    when(termNameHasApplyMethod(eqTree(termName))).thenReturn(true)
    when(applyDeclDefInferrer.infer(eqTree(adjustedTermApply), eqTo(context))).thenReturn(ThePartialDeclDef)

    internalApplyDeclDefInferrer.infer(termApply, context) should equalPartialDeclDef(ThePartialDeclDef)
  }

  test("infer() when 'fun' is a Term.ApplyType with a Term.Name having an 'apply' method") {
    val termName = q"foo"
    val termApply = q"foo[Int](x)"
    val adjustedTermApply = q"foo.apply[Int](x)"

    when(termNameHasApplyMethod(eqTree(termName))).thenReturn(true)
    when(applyDeclDefInferrer.infer(eqTree(adjustedTermApply), eqTo(context))).thenReturn(ThePartialDeclDef)

    internalApplyDeclDefInferrer.infer(termApply, context) should equalPartialDeclDef(ThePartialDeclDef)
  }

  test("infer() when 'fun' is a Term.Name with no 'apply' method") {
    val termName = q"foo"
    val termApply = q"foo(x)"

    when(termNameHasApplyMethod(eqTree(termName))).thenReturn(false)
    when(applyDeclDefInferrer.infer(eqTree(termApply), eqTo(context))).thenReturn(ThePartialDeclDef)

    internalApplyDeclDefInferrer.infer(termApply, context) should equalPartialDeclDef(ThePartialDeclDef)
  }

  test("infer() when 'fun' is a Term.ApplyType with a Term.Name, which does not have an 'apply' method") {
    val termName = q"foo"
    val termApply = q"foo[Int](x)"

    when(termNameHasApplyMethod(eqTree(termName))).thenReturn(false)
    when(applyDeclDefInferrer.infer(eqTree(termApply), eqTo(context))).thenReturn(ThePartialDeclDef)

    internalApplyDeclDefInferrer.infer(termApply, context) should equalPartialDeclDef(ThePartialDeclDef)
  }

  test("infer() when 'fun' is a Term.Select") {
    val termApply = q"foo.bar(3)"

    when(applyDeclDefInferrer.infer(eqTree(termApply), eqTo(context))).thenReturn(ThePartialDeclDef)

    internalApplyDeclDefInferrer.infer(termApply, context) should equalPartialDeclDef(ThePartialDeclDef)
  }
}
