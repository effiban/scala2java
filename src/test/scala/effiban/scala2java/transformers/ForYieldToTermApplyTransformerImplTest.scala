package effiban.scala2java.transformers

import effiban.scala2java.testsuites.UnitTestSuite

import scala.meta.Term

class ForYieldToTermApplyTransformerImplTest extends UnitTestSuite {

  private val patToTermParamTransformer = mock[PatToTermParamTransformer]

  private val forYieldToTermApplyTransformer = new ForYieldToTermApplyTransformerImpl(patToTermParamTransformer)

  test("intermediateFunctionName should be 'flatMap'") {
    forYieldToTermApplyTransformer.intermediateFunctionName.structure shouldBe Term.Name("flatMap").structure
  }

  test("finalFunctionName should be 'map'") {
    forYieldToTermApplyTransformer.finalFunctionName.structure shouldBe Term.Name("map").structure
  }
}
