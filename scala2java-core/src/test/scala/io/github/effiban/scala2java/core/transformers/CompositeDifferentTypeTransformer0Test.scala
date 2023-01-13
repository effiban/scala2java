package io.github.effiban.scala2java.core.transformers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.spi.transformers.DifferentTypeTransformer0

class CompositeDifferentTypeTransformer0Test extends UnitTestSuite {

  private val InputObj = TestInputObj("fun")
  private val OutputObj = TestOutputObj("fun")

  private val transformer1 = mock[TestInputToOutputObjTransformer]
  private val transformer2 = mock[TestInputToOutputObjTransformer]

  test("transform when there are no transformers - should return empty") {
    compositeTransformer().transform(InputObj) shouldBe None
  }

  test("transform when there is one transformer returning non-empty should return its result") {
    when(transformer1.transform(InputObj)).thenReturn(Some(OutputObj))

    compositeTransformer(List(transformer1)).transform(InputObj).value shouldBe OutputObj
  }

  test("transform when there are two transformers and first returns non-empty should return result of first") {
    when(transformer1.transform(InputObj)).thenReturn(Some(OutputObj))

    compositeTransformer(List(transformer1, transformer2)).transform(InputObj).value shouldBe OutputObj
  }

  test("transform when there are two transformers, first returns empty and second returns non-empty - should return result of second") {
    when(transformer1.transform(InputObj)).thenReturn(None)
    when(transformer2.transform(InputObj)).thenReturn(Some(OutputObj))

    compositeTransformer(List(transformer1, transformer2)).transform(InputObj).value shouldBe OutputObj
  }

  test("transform when there are two transformers, both returning empty - should return empty") {
    when(transformer1.transform(InputObj)).thenReturn(None)
    when(transformer2.transform(InputObj)).thenReturn(None)

    compositeTransformer(List(transformer1, transformer2)).transform(InputObj) shouldBe None
  }

  private def compositeTransformer(transformers: List[TestInputToOutputObjTransformer] = Nil) = new CompositeTestInputToOutputObjTransformer(transformers)


  private case class TestInputObj(name: String)
  private case class TestOutputObj(name: String)

  private sealed trait TestInputToOutputObjTransformer extends DifferentTypeTransformer0[TestInputObj, TestOutputObj]

  private class CompositeTestInputToOutputObjTransformer(override protected val transformers: List[DifferentTypeTransformer0[TestInputObj, TestOutputObj]] = Nil)
    extends CompositeDifferentTypeTransformer0[TestInputObj, TestOutputObj] with TestInputToOutputObjTransformer
}
