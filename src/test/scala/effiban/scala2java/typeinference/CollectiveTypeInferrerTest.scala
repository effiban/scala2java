package effiban.scala2java.typeinference

import effiban.scala2java.testsuites.UnitTestSuite
import effiban.scala2java.testtrees.TypeNames

import scala.meta.Type

class CollectiveTypeInferrerTest extends UnitTestSuite {

  test("infer for two defined identical types should return the type") {
    CollectiveTypeInferrer.inferScalar(List(Some(TypeNames.Int), Some(TypeNames.Int))).value.structure shouldBe TypeNames.Int.structure
  }

  test("infer for two defined and equal (non-identical) types should return the type") {
    CollectiveTypeInferrer.inferScalar(List(Some(TypeNames.Int), Some(Type.Name("Int")))).value.structure shouldBe TypeNames.Int.structure
  }

  test("infer for two defined and different types should return None") {
    CollectiveTypeInferrer.inferScalar(List(Some(TypeNames.Int), Some(TypeNames.String))) shouldBe None
  }

  test("infer for two anonymous types should return the anonymous type") {
    CollectiveTypeInferrer.inferScalar(List(Some(Type.AnonymousName()), Some(Type.AnonymousName()))).value.structure shouldBe Type.AnonymousName().structure
  }

  test("infer for two None elements should return None") {
    CollectiveTypeInferrer.inferScalar(List(None, None)) shouldBe None
  }

  test("infer for two defined identical types and an anonymous type should return the defined type") {
    val maybeTypes = List(Some(TypeNames.String), Some(TypeNames.String), Some(Type.AnonymousName()))

    CollectiveTypeInferrer.inferScalar(maybeTypes).value.structure shouldBe TypeNames.String.structure
  }

  test("infer for two defined identical types and a None should return None") {
    val maybeTypes = List(Some(TypeNames.String), Some(TypeNames.String), None)

    CollectiveTypeInferrer.inferScalar(maybeTypes) shouldBe None
  }

  test("infer when empty should return the anonymous type") {
    CollectiveTypeInferrer.inferScalar(Nil).value.structure shouldBe Type.AnonymousName().structure
  }
}
