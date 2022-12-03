package io.github.effiban.scala2java.core.transformers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite

import scala.meta.Term

class BasicTermApplyInfixToTermApplyTransformerTest extends UnitTestSuite {

  test("transform") {
    val arg1 = Term.Name("arg1")
    val arg2 = Term.Name("arg2")
    val arg3 = Term.Name("arg3")
    val fun = Term.Name("foo")

    val termApplyInfix = Term.ApplyInfix(
      lhs = arg1,
      op = fun,
      targs = Nil,
      args = List(arg2, arg3)
    )

    val expectedTermApply = Term.Apply(
      fun = fun,
      args = List(arg1, arg2, arg3)
    )

    BasicTermApplyInfixToTermApplyTransformer.transform(termApplyInfix).value.structure shouldBe expectedTermApply.structure
  }

}
