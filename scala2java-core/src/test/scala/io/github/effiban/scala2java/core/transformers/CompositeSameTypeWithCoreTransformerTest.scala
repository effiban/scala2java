package io.github.effiban.scala2java.core.transformers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.spi.transformers.SameTypeTransformer

class CompositeSameTypeWithCoreTransformerTest extends UnitTestSuite {

  private val InitialObj = TestObj("Initial")

  private val coreTransformer = mock[TestObjTransformer]

  test("transform when there are two other transformers") {
    val transformedObj1 = TestObj("Transformed1")
    val transformedObj2 = TestObj("Transformed2")
    val transformedObj3 = TestObj("Transformed3")

    val otherTransformer1 = mock[TestObjTransformer]
    val otherTransformer2 = mock[TestObjTransformer]
    val otherTransformers = List(otherTransformer1, otherTransformer2)

    when(otherTransformer1.transform(InitialObj)).thenReturn(transformedObj1)
    when(otherTransformer2.transform(transformedObj1)).thenReturn(transformedObj2)
    when(coreTransformer.transform(transformedObj2)).thenReturn(transformedObj3)

    compositeTransformer(otherTransformers).transform(InitialObj) shouldBe transformedObj3
  }

  test("transform when there is one other transformer") {
    val transformedObj1 = TestObj("Transformed1")
    val transformedObj2 = TestObj("Transformed2")

    val otherTransformer = mock[TestObjTransformer]
    val otherTransformers = List(otherTransformer)

    when(otherTransformer.transform(InitialObj)).thenReturn(transformedObj1)
    when(coreTransformer.transform(transformedObj1)).thenReturn(transformedObj2)

    compositeTransformer(otherTransformers).transform(InitialObj) shouldBe transformedObj2
  }

  test("transform when there are no other transformers") {
    val transformedObj = TestObj("Transformed")

    when(coreTransformer.transform(InitialObj)).thenReturn(transformedObj)

    compositeTransformer().transform(InitialObj) shouldBe transformedObj
  }

  private def compositeTransformer(otherTransformers: List[TestObjTransformer] = Nil) =
    new CompositeTestObjTransformer(coreTransformer, otherTransformers)

  private case class TestObj(name: String)

  private sealed trait TestObjTransformer extends SameTypeTransformer[TestObj]

  private class CompositeTestObjTransformer(override protected val coreTransformer: SameTypeTransformer[TestObj],
                                            override protected val otherTransformers: List[SameTypeTransformer[TestObj]] = Nil)
    extends CompositeSameTypeWithCoreTransformer[TestObj] with TestObjTransformer
}

