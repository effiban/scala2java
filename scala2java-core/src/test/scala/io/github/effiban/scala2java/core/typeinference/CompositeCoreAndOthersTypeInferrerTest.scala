package io.github.effiban.scala2java.core.typeinference

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.spi.typeinferrers.TypeInferrer

import scala.meta.Type

class CompositeCoreAndOthersTypeInferrerTest extends UnitTestSuite {

  private val TheTestObj = TestObj("bla")
  private val TheType = Type.Name("MyType")

  private val coreInferrer = mock[TestObjTypeInferrer]
  private val otherInferrer1 = mock[TestObjTypeInferrer]
  private val otherInferrer2 = mock[TestObjTypeInferrer]

  test("infer when there are no other inferrers and core inferrer returns None - should return None") {
    when(coreInferrer.infer(TheTestObj)).thenReturn(None)

    compositeInferrer().infer(TheTestObj) shouldBe None
  }

  test("infer when there are no other inferrers and core inferrer returns a type - should return it") {
    when(coreInferrer.infer(TheTestObj)).thenReturn(Some(TheType))

    compositeInferrer().infer(TheTestObj).value.structure shouldBe TheType.structure
  }

  test("infer when there is one other inferrer returning None, and core returns a type - should return it") {
    when(otherInferrer1.infer(TheTestObj)).thenReturn(None)
    when(coreInferrer.infer(TheTestObj)).thenReturn(Some(TheType))

    compositeInferrer(List(otherInferrer1)).infer(TheTestObj).value.structure shouldBe TheType.structure
  }

  test("infer when there is one other inferrer returning a type - should return it") {
    when(otherInferrer1.infer(TheTestObj)).thenReturn(Some(TheType))

    compositeInferrer(List(otherInferrer1)).infer(TheTestObj).value.structure shouldBe TheType.structure
  }

  test("infer when there is one other inferrer returning a type - should not invoke the core inferrer") {
    when(otherInferrer1.infer(TheTestObj)).thenReturn(Some(TheType))

    compositeInferrer(List(otherInferrer1)).infer(TheTestObj)

    verifyNoMoreInteractions(coreInferrer)
  }

  test("infer when there are two other inferrers, both returning None, and the core also returns None - should return None") {
    when(otherInferrer1.infer(TheTestObj)).thenReturn(None)
    when(otherInferrer2.infer(TheTestObj)).thenReturn(None)
    when(coreInferrer.infer(TheTestObj)).thenReturn(None)

    compositeInferrer(List(otherInferrer1, otherInferrer2)).infer(TheTestObj) shouldBe None
  }

  test("infer when there are two other inferrers, both returning None, and the core returns a type - should return None") {
    when(otherInferrer1.infer(TheTestObj)).thenReturn(None)
    when(otherInferrer2.infer(TheTestObj)).thenReturn(None)
    when(coreInferrer.infer(TheTestObj)).thenReturn(Some(TheType))

    compositeInferrer(List(otherInferrer1, otherInferrer2)).infer(TheTestObj).value.structure shouldBe TheType.structure
  }

  test("infer when there are two other inferrers, first returns None and second returns a type - should return it") {
    when(otherInferrer1.infer(TheTestObj)).thenReturn(None)
    when(otherInferrer2.infer(TheTestObj)).thenReturn(Some(TheType))

    compositeInferrer(List(otherInferrer1, otherInferrer2)).infer(TheTestObj).value.structure shouldBe TheType.structure
  }

  test("infer when there are two other inferrers, first returns None and second returns a type - should not invoke the core inferrer") {
    when(otherInferrer1.infer(TheTestObj)).thenReturn(None)
    when(otherInferrer2.infer(TheTestObj)).thenReturn(Some(TheType))

    compositeInferrer(List(otherInferrer1, otherInferrer2)).infer(TheTestObj)

    verifyNoMoreInteractions(coreInferrer)
  }

  test("infer when there are two other inferrers and first returns a type - should return it") {
    when(otherInferrer1.infer(TheTestObj)).thenReturn(Some(TheType))

    compositeInferrer(List(otherInferrer1, otherInferrer2)).infer(TheTestObj).value.structure shouldBe TheType.structure
  }

  test("infer when there are two other inferrers and first returns a type - should not invoke any other inferrer") {
    when(otherInferrer1.infer(TheTestObj)).thenReturn(Some(TheType))

    compositeInferrer(List(otherInferrer1, otherInferrer2)).infer(TheTestObj)

    verifyNoMoreInteractions(otherInferrer2, coreInferrer)
  }

  private def compositeInferrer(otherInferrers: List[TestObjTypeInferrer] = Nil) = {
    new CompositeTestObjTypeInferrer(coreInferrer, otherInferrers)
  }

  private case class TestObj(name: String)

  private sealed trait TestObjTypeInferrer extends TypeInferrer[TestObj]

  private class CompositeTestObjTypeInferrer(theCoreInferrer: => TestObjTypeInferrer,
                                             override protected val otherInferrers: List[TestObjTypeInferrer] = Nil)
    extends CompositeCoreAndOthersTypeInferrer[TestObj] with TestObjTypeInferrer {
    override protected def coreInferrer(): TypeInferrer[TestObj] = theCoreInferrer
  }
}
