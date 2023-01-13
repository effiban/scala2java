package io.github.effiban.scala2java.core.transformers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.spi.transformers.SameTypeTransformer1

class CompositeSameTypeTransformer1Test extends UnitTestSuite {

  private val InputObj = TestObj("input")
  private val InputArg = TestArg("arg")

  private val OutputObj1 = TestObj("output1")
  private val OutputObj2 = TestObj("output2")

  private val transformer1 = mock[TestObjTransformer]
  private val transformer2 = mock[TestObjTransformer]

  test("transform when there are two transformers") {
    val transformers = List(transformer1, transformer2)

    when(transformer1.transform(InputObj, InputArg)).thenReturn(OutputObj1)
    when(transformer2.transform(OutputObj1, InputArg)).thenReturn(OutputObj2)

    new CompositeTestObjTransformer(transformers).transform(InputObj, InputArg) shouldBe OutputObj2
  }

  test("transform when there is one transformer") {
    val transformers = List(transformer1)

    when(transformer1.transform(InputObj, InputArg)).thenReturn(OutputObj1)

    new CompositeTestObjTransformer(transformers).transform(InputObj, InputArg) shouldBe OutputObj1
  }

  test("transform when there are no transformers") {
    new CompositeTestObjTransformer().transform(InputObj, InputArg) shouldBe InputObj
  }


  private case class TestObj(name: String)
  private case class TestArg(name: String)

  private sealed trait TestObjTransformer extends SameTypeTransformer1[TestObj, TestArg]

  private class CompositeTestObjTransformer(override protected val transformers: List[SameTypeTransformer1[TestObj, TestArg]] = Nil)
    extends CompositeSameTypeTransformer1[TestObj, TestArg] with TestObjTransformer

}
