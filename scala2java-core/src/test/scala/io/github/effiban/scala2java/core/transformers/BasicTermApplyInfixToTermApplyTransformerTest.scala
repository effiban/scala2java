package io.github.effiban.scala2java.core.transformers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite

import scala.meta.{Term, XtensionQuasiquoteTerm}

class BasicTermApplyInfixToTermApplyTransformerTest extends UnitTestSuite {

  test("transform with one arg") {
    val termApplyInfix = q"obj fun arg"
    val expectedTermApply = q"obj.fun(arg)"

    BasicTermApplyInfixToTermApplyTransformer.transform(termApplyInfix).value.structure shouldBe expectedTermApply.structure
  }

  test("transform with two args") {
    val termApplyInfix = q"obj fun(arg1,arg2)"
    val expectedTermApply = q"obj.fun(arg1, arg2)"

    BasicTermApplyInfixToTermApplyTransformer.transform(termApplyInfix).value.structure shouldBe expectedTermApply.structure
  }
}
