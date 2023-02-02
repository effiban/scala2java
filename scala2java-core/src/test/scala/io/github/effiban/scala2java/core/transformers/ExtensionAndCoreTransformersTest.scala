package io.github.effiban.scala2java.core.transformers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite

class ExtensionAndCoreTransformersTest extends UnitTestSuite {

  test("transformers should have the core transformer last") {
    val otherTransformer1 = mock[TestTransformer]
    val otherTransformer2 = mock[TestTransformer]
    val aCoreTransformer = mock[TestTransformer]

    val extAndCoreTransformers = new ExtensionAndCoreTransformers[TestTransformer] {
      override protected val extensionTransformers: List[TestTransformer] = List(otherTransformer1, otherTransformer2)
      override protected val coreTransformer: TestTransformer = aCoreTransformer
    }

    extAndCoreTransformers.transformers shouldBe List(otherTransformer1, otherTransformer2, aCoreTransformer)
  }

  private trait TestTransformer
}
