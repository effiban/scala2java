package effiban.scala2java.typeinference

import effiban.scala2java.testsuites.UnitTestSuite
import effiban.scala2java.testtrees.TypeNames

import scala.meta.Type

class CollectiveTypeInferrerTest extends UnitTestSuite {

  test("infer for two defined and equal types should return the type") {
    CollectiveTypeInferrer.infer(List(Some(TypeNames.Int), Some(TypeNames.Int))).value.structure shouldBe TypeNames.Int.structure
  }

  test("infer for two defined and different types should return None") {
    CollectiveTypeInferrer.infer(List(Some(TypeNames.Int), Some(TypeNames.String))) shouldBe None
  }

  test("infer for two anonymous types should return the anonymous type") {
    CollectiveTypeInferrer.infer(List(Some(Type.AnonymousName()), Some(Type.AnonymousName()))).value.structure shouldBe Type.AnonymousName().structure
  }

  test("infer for two None elements should return None") {
    CollectiveTypeInferrer.infer(List(None, None)) shouldBe None
  }

  test("infer for two defined and equal types and an anonymous type should return the defined type") {
    val maybeTypes = List(Some(TypeNames.String), Some(TypeNames.String), Some(Type.AnonymousName()))

    CollectiveTypeInferrer.infer(maybeTypes).value.structure shouldBe TypeNames.String.structure
  }

  test("infer for two defined and equal types and a None should return None") {
    val maybeTypes = List(Some(TypeNames.String), Some(TypeNames.String), None)

    CollectiveTypeInferrer.infer(maybeTypes) shouldBe None
  }

  test("infer when empty should return the anonymous type") {
    CollectiveTypeInferrer.infer(Nil).value.structure shouldBe Type.AnonymousName().structure
  }
}
