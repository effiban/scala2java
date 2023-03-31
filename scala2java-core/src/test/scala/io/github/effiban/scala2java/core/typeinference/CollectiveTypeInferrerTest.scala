package io.github.effiban.scala2java.core.typeinference

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.testtrees.TypeNames
import io.github.effiban.scala2java.core.typeinference.CollectiveTypeInferrer.{inferScalar, inferTuple}

import scala.meta.Type

class CollectiveTypeInferrerTest extends UnitTestSuite {

  private val ScalarScenarios = Table(
    ("MaybeScalarTypesDesc", "ExpectedMaybeTypeDesc", "MaybeScalarTypes", "ExpectedMaybeType"),
    ("two defined identical types", "same type", List(Some(TypeNames.Int), Some(TypeNames.Int)), Some(TypeNames.Int)),
    ("two defined and equal (non-identical) types", "the type", List(Some(TypeNames.Int), Some(Type.Name("Int"))), Some(TypeNames.Int)),
    ("two defined and different types", "None", List(Some(TypeNames.Int), Some(TypeNames.String)), None),
    ("two anonymous types", "the anonymous type",
      List(Some(Type.AnonymousName()), Some(Type.AnonymousName())), Some(Type.AnonymousName())),
    ("two None elements", "None", List(None, None), None),
    ("two defined identical types and an anonymous type", "the defined type",
      List(Some(TypeNames.String), Some(TypeNames.String), Some(Type.AnonymousName())), Some(TypeNames.String)),
    ("two defined identical types and a None", "None", List(Some(TypeNames.String), Some(TypeNames.String), None), None),
    ("empty", "None", Nil, None)
  )

  private val TypeTupleStrIntA = Type.Tuple(List(TypeNames.String, TypeNames.Int))
  private val TypeTupleStrIntB = Type.Tuple(List(TypeNames.String, TypeNames.Int))
  private val TypeTupleStrLong = Type.Tuple(List(TypeNames.String, TypeNames.Long))
  private val TypeTupleDoubleLong = Type.Tuple(List(TypeNames.Double, TypeNames.Long))
  private val TypeTupleStrAny = Type.Tuple(List(TypeNames.String, TypeNames.ScalaAny))
  private val TypeTupleAnyAny = Type.Tuple(List(TypeNames.ScalaAny, TypeNames.ScalaAny))

  private val TupleValidScenarios = Table(
    ("TupleTypesDesc", "ExpectedTupleTypeDesc", "TupleTypes", "ExpectedTupleType"),
    ("one tuple", "the same tuple", List(TypeTupleStrIntA), TypeTupleStrIntA),
    ("two identical tuples", "the same tuple", List(TypeTupleStrIntA, TypeTupleStrIntA), TypeTupleStrIntA),
    ("two tuples with equal types", "the same tuple", List(TypeTupleStrIntA, TypeTupleStrIntB), TypeTupleStrIntA),
    ("two tuples with completely different types", "A tuple with all types 'Any'",
      List(TypeTupleStrIntA, TypeTupleDoubleLong), TypeTupleAnyAny),
    ("two tuples with some matching types", "A tuple with matching types and others 'Any'",
      List(TypeTupleStrIntA, TypeTupleStrLong), TypeTupleStrAny)
  )

  forAll(ScalarScenarios) {
    case (maybeScalarTypesDesc: String, expectedMaybeTypeDesc: String, maybeScalarTypes: List[Option[Type]], expectedMaybeType: Option[Type]) =>
      test(s"infer for $maybeScalarTypesDesc should return $expectedMaybeTypeDesc") {
        inferScalar(maybeScalarTypes).structure shouldBe expectedMaybeType.structure
      }
  }

  forAll(TupleValidScenarios) {
    case (tupleTypesDesc: String, expectedTupleTypeDesc: String, tupleTypes: List[Type.Tuple], expectedTupleType: Type.Tuple) =>
      test(s"infer for $tupleTypesDesc should return $expectedTupleTypeDesc") {
        inferTuple(tupleTypes).structure shouldBe expectedTupleType.structure
      }
  }

  test("inferTuple when tuples have different sizes should throw exception") {
    intercept[IllegalStateException] {
      inferTuple(List(Type.Tuple(List(TypeNames.String)), Type.Tuple(List(TypeNames.String, TypeNames.Int))))
    }
  }

  test("inferTuple when empty should throw exception") {
    intercept[IllegalStateException] {
      inferTuple(List.empty)
    }
  }
}
