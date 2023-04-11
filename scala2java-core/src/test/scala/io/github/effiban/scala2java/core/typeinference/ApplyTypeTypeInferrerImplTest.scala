package io.github.effiban.scala2java.core.typeinference

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.testtrees.TermNames.ScalaClassOf
import io.github.effiban.scala2java.core.testtrees.TypeNames
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.{Term, Type, XtensionQuasiquoteTerm, XtensionQuasiquoteType}

class ApplyTypeTypeInferrerImplTest extends UnitTestSuite {

  private val applyReturnTypeInferrer = mock[ApplyReturnTypeInferrer]

  private val applyTypeTypeInferrer = new ApplyTypeTypeInferrerImpl(applyReturnTypeInferrer)

  test("infer() when term is 'classOf[Foo]' should return 'Class[Foo]'") {
    val innerType = t"Foo"
    val termApplyType = Term.ApplyType(ScalaClassOf, List(innerType))
    val expectedType = Type.Apply(TypeNames.Class, List(innerType))

    applyTypeTypeInferrer.infer(termApplyType).value.structure shouldBe expectedType.structure
  }

  test("infer() for arbitrary typed term should return an equivalent Term.Apply with no params") {
    val termApplyType = q"Foo[Int]"
    val expectedTermApply = q"Foo[Int]()"
    val expectedType = t"String"

    when(applyReturnTypeInferrer.infer(eqTree(expectedTermApply))).thenReturn(Some(expectedType))

    applyTypeTypeInferrer.infer(termApplyType).value.structure shouldBe expectedType.structure
  }
}
