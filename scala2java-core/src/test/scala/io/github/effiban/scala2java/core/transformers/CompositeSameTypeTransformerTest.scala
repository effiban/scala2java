package io.github.effiban.scala2java.core.transformers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.spi.transformers.SameTypeTransformer

class CompositeSameTypeTransformerTest extends UnitTestSuite {

  private val InputObj = TestObj("input")

  private val OutputObj1 = TestObj("output1")
  private val OutputObj2 = TestObj("output2")

  private val transformer1 = mock[TestObjTransformer]
  private val transformer2 = mock[TestObjTransformer]

  test("transform when there are two transformers") {
    val transformers = List(transformer1, transformer2)

    when(transformer1.transform(InputObj)).thenReturn(OutputObj1)
    when(transformer2.transform(OutputObj1)).thenReturn(OutputObj2)

    new CompositeTestObjTransformer(transformers).transform(InputObj) shouldBe OutputObj2
  }

  test("transform when there is one transformer") {
    val transformers = List(transformer1)

    when(transformer1.transform(InputObj)).thenReturn(OutputObj1)

    new CompositeTestObjTransformer(transformers).transform(InputObj) shouldBe OutputObj1
  }

  test("transform when there are no transformers") {
    new CompositeTestObjTransformer().transform(InputObj) shouldBe InputObj
  }
}
private case class TestObj(name: String)

private sealed trait TestObjTransformer extends SameTypeTransformer[TestObj]

private class CompositeTestObjTransformer(override protected val transformers: List[SameTypeTransformer[TestObj]] = Nil)
  extends CompositeSameTypeTransformer[TestObj] with TestObjTransformer

