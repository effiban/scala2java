package io.github.effiban.scala2java.core.typeinference

import io.github.effiban.scala2java.core.predicates.TermNameHasApplyMethod
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.{XtensionQuasiquoteTerm, XtensionQuasiquoteType}

class ApplyTypeTypeInferrerImplTest extends UnitTestSuite {

  private val applyReturnTypeInferrer = mock[ApplyReturnTypeInferrer]
  private val termNameHasApplyMethod = mock[TermNameHasApplyMethod]

  private val applyTypeTypeInferrer = new ApplyTypeTypeInferrerImpl(applyReturnTypeInferrer, termNameHasApplyMethod)

  test("infer() when term has an apply() method") {
    val termApplyType = q"List[Int]"
    val expectedTermApply = q"List.apply[Int]()"
    val expectedType = t"List[Int]"

    when(termNameHasApplyMethod(eqTree(q"List"))).thenReturn(true)
    when(applyReturnTypeInferrer.infer(eqTree(expectedTermApply))).thenReturn(Some(expectedType))

    applyTypeTypeInferrer.infer(termApplyType).value.structure shouldBe expectedType.structure
  }

  test("infer() when term does not have an apply() method") {
    val termApplyType = q"Foo[Int]"
    val expectedTermApply = q"Foo[Int]()"
    val expectedType = t"String"

    when(termNameHasApplyMethod(eqTree(q"Foo"))).thenReturn(false)
    when(applyReturnTypeInferrer.infer(eqTree(expectedTermApply))).thenReturn(Some(expectedType))

    applyTypeTypeInferrer.infer(termApplyType).value.structure shouldBe expectedType.structure
  }
}
