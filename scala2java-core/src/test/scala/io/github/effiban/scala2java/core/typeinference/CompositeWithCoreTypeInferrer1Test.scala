package io.github.effiban.scala2java.core.typeinference

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.spi.typeinferrers.TypeInferrer1

import scala.meta.{Type, XtensionQuasiquoteType}

class CompositeWithCoreTypeInferrer1Test extends UnitTestSuite {

  private val TheTestObj = TestObj("bla")
  private val InputType = t"MyInputType"
  private val OutputType = t"MyOutputType"

  private val coreInferrer = mock[TestObjTypeInferrer]
  private val otherInferrer1 = mock[TestObjTypeInferrer]
  private val otherInferrer2 = mock[TestObjTypeInferrer]

  test("infer when there are no other inferrers and core inferrer returns None - should return None") {
    when(coreInferrer.infer(TheTestObj, InputType)).thenReturn(None)

    compositeInferrer().infer(TheTestObj, InputType) shouldBe None
  }

  test("infer when there are no other inferrers and core inferrer returns a type - should return it") {
    when(coreInferrer.infer(TheTestObj, InputType)).thenReturn(Some(OutputType))

    compositeInferrer().infer(TheTestObj, InputType).value.structure shouldBe OutputType.structure
  }

  test("infer when there is one other inferrer returning None, and core returns a type - should return it") {
    when(otherInferrer1.infer(TheTestObj, InputType)).thenReturn(None)
    when(coreInferrer.infer(TheTestObj, InputType)).thenReturn(Some(OutputType))

    compositeInferrer(List(otherInferrer1)).infer(TheTestObj, InputType).value.structure shouldBe OutputType.structure
  }

  test("infer when there is one other inferrer returning a type - should return it") {
    when(otherInferrer1.infer(TheTestObj, InputType)).thenReturn(Some(OutputType))

    compositeInferrer(List(otherInferrer1)).infer(TheTestObj, InputType).value.structure shouldBe OutputType.structure
  }

  test("infer when there is one other inferrer returning a type - should not invoke the core inferrer") {
    when(otherInferrer1.infer(TheTestObj, InputType)).thenReturn(Some(OutputType))

    compositeInferrer(List(otherInferrer1)).infer(TheTestObj, InputType)

    verifyNoMoreInteractions(coreInferrer)
  }

  test("infer when there are two other inferrers, both returning None, and the core also returns None - should return None") {
    when(otherInferrer1.infer(TheTestObj, InputType)).thenReturn(None)
    when(otherInferrer2.infer(TheTestObj, InputType)).thenReturn(None)
    when(coreInferrer.infer(TheTestObj, InputType)).thenReturn(None)

    compositeInferrer(List(otherInferrer1, otherInferrer2)).infer(TheTestObj, InputType) shouldBe None
  }

  test("infer when there are two other inferrers, both returning None, and the core returns a type - should return None") {
    when(otherInferrer1.infer(TheTestObj, InputType)).thenReturn(None)
    when(otherInferrer2.infer(TheTestObj, InputType)).thenReturn(None)
    when(coreInferrer.infer(TheTestObj, InputType)).thenReturn(Some(OutputType))

    compositeInferrer(List(otherInferrer1, otherInferrer2)).infer(TheTestObj, InputType).value.structure shouldBe OutputType.structure
  }

  test("infer when there are two other inferrers, first returns None and second returns a type - should return it") {
    when(otherInferrer1.infer(TheTestObj, InputType)).thenReturn(None)
    when(otherInferrer2.infer(TheTestObj, InputType)).thenReturn(Some(OutputType))

    compositeInferrer(List(otherInferrer1, otherInferrer2)).infer(TheTestObj, InputType).value.structure shouldBe OutputType.structure
  }

  test("infer when there are two other inferrers, first returns None and second returns a type - should not invoke the core inferrer") {
    when(otherInferrer1.infer(TheTestObj, InputType)).thenReturn(None)
    when(otherInferrer2.infer(TheTestObj, InputType)).thenReturn(Some(OutputType))

    compositeInferrer(List(otherInferrer1, otherInferrer2)).infer(TheTestObj, InputType)

    verifyNoMoreInteractions(coreInferrer)
  }

  test("infer when there are two other inferrers and first returns a type - should return it") {
    when(otherInferrer1.infer(TheTestObj, InputType)).thenReturn(Some(OutputType))

    compositeInferrer(List(otherInferrer1, otherInferrer2)).infer(TheTestObj, InputType).value.structure shouldBe OutputType.structure
  }

  test("infer when there are two other inferrers and first returns a type - should not invoke any other inferrer") {
    when(otherInferrer1.infer(TheTestObj, InputType)).thenReturn(Some(OutputType))

    compositeInferrer(List(otherInferrer1, otherInferrer2)).infer(TheTestObj, InputType)

    verifyNoMoreInteractions(otherInferrer2, coreInferrer)
  }

  private def compositeInferrer(otherInferrers: List[TestObjTypeInferrer] = Nil) = {
    new CompositeTestObjTypeInferrer(coreInferrer, otherInferrers)
  }

  private case class TestObj(name: String)

  private sealed trait TestObjTypeInferrer extends TypeInferrer1[TestObj, Type]

  private class CompositeTestObjTypeInferrer(theCoreInferrer: => TestObjTypeInferrer,
                                             override protected val otherInferrers: List[TestObjTypeInferrer] = Nil)
    extends CompositeWithCoreTypeInferrer1[TestObj, Type] with TestObjTypeInferrer {
    override protected def coreInferrer(): TypeInferrer1[TestObj, Type] = theCoreInferrer
  }
}
