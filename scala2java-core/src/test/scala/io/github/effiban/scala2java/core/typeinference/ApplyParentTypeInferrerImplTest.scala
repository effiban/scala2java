package io.github.effiban.scala2java.core.typeinference

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.{XtensionQuasiquoteTerm, XtensionQuasiquoteType}

class ApplyParentTypeInferrerImplTest extends UnitTestSuite {

  private val qualifierTypeInferrer = mock[QualifierTypeInferrer]

  private val applyParentTypeInferrer = new ApplyParentTypeInferrerImpl(qualifierTypeInferrer)

  test("infer when fun is a Term.Select should return the type of the qualifier") {
    val termApply = q"foo.bar(2)"
    val termSelect = q"foo.bar"
    val expectedQualifierType = t"Foo"

    when(qualifierTypeInferrer.infer(eqTree(termSelect))).thenReturn(Some(expectedQualifierType))

    applyParentTypeInferrer.infer(termApply).value.structure shouldBe expectedQualifierType.structure
  }

  test("infer when fun is a Term.ApplyType wrapping a Term.Select, should return the type of the select qualifier") {
    val termApply = q"foo.bar[Int](2)"
    val termSelect = q"foo.bar"
    val expectedQualifierType = t"Foo"

    when(qualifierTypeInferrer.infer(eqTree(termSelect))).thenReturn(Some(expectedQualifierType))

    applyParentTypeInferrer.infer(termApply).value.structure shouldBe expectedQualifierType.structure
  }

  test("infer when fun is a Term.Name should return None") {
    val termApply = q"foo(2)"

    applyParentTypeInferrer.infer(termApply) shouldBe None
  }
}
