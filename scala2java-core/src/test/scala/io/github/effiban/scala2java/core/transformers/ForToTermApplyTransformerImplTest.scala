package io.github.effiban.scala2java.core.transformers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite

import scala.meta.Term

class ForToTermApplyTransformerImplTest extends UnitTestSuite {

  private final val ForEachFunctionName: Term.Name = Term.Name("forEach")

  private val patToTermParamTransformer = mock[PatToTermParamTransformer]

  private val forToTermApplyTransformer = new ForToTermApplyTransformerImpl(patToTermParamTransformer)

  test("intermediateFunctionName should be 'forEach'") {
    forToTermApplyTransformer.intermediateFunctionName.structure shouldBe ForEachFunctionName.structure
  }

  test("finalFunctionName should be 'forEach'") {
    forToTermApplyTransformer.finalFunctionName.structure shouldBe ForEachFunctionName.structure
  }
}
