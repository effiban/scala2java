package io.github.effiban.scala2java.core.typeinference

import io.github.effiban.scala2java.core.entities.TypeSelects._
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.CombinedMatchers.{eqOptionTreeList, eqTreeList}

import scala.meta.Type

class CompositeCollectiveTypeInferrerImplTest extends UnitTestSuite {

  private val collectiveTypeInferrer = mock[CollectiveTypeInferrer]

  private val compositeCollectiveTypeInferrer = new CompositeCollectiveTypeInferrerImpl(collectiveTypeInferrer)

  test("infer when all types are scalars and have a collective type, should return it") {
    val maybeTypes = List(ScalaInt, ScalaInt).map(Some(_))

    when(collectiveTypeInferrer.inferScalar(eqOptionTreeList(maybeTypes))).thenReturn(Some(ScalaInt))

    compositeCollectiveTypeInferrer.infer(maybeTypes).structure shouldBe ScalaInt.structure
  }

  test("infer when all types are scalars and don't have a collective type, should return Any") {
    val maybeTypes = List(ScalaInt, ScalaString).map(Some(_))

    when(collectiveTypeInferrer.inferScalar(eqOptionTreeList(maybeTypes))).thenReturn(None)

    compositeCollectiveTypeInferrer.infer(maybeTypes).structure shouldBe ScalaAny.structure
  }

  test("infer when all types are tuples and have a collective type, should return it") {
    val tupleType = Type.Tuple(List(ScalaString, ScalaInt))
    val tupleTypes = List(tupleType, tupleType)

    when(collectiveTypeInferrer.inferTuple(eqTreeList(tupleTypes))).thenReturn(tupleType)

    compositeCollectiveTypeInferrer.infer(tupleTypes.map(Some(_))).structure shouldBe tupleType.structure
  }

  test("infer when all types are tuples and do not have a collective type, should return a tuple of Any-s") {
    val tupleType1 = Type.Tuple(List(ScalaString, ScalaInt))
    val tupleType2 = Type.Tuple(List(ScalaShort, ScalaLong))
    val tupleTypeAny = Type.Tuple(List(ScalaAny, ScalaAny))

    val inputTupleTypes = List(tupleType1, tupleType2)

    when(collectiveTypeInferrer.inferTuple(eqTreeList(inputTupleTypes))).thenReturn(tupleTypeAny)

    compositeCollectiveTypeInferrer.infer(inputTupleTypes.map(Some(_))).structure shouldBe tupleTypeAny.structure
  }

  test("infer when some types are tuples and some scalars, should return result of inferring as scalars") {
    val tupleType1 = Type.Tuple(List(ScalaString, ScalaInt))
    val tupleType2 = Type.Tuple(List(ScalaShort, ScalaLong))
    val scalarType1 = ScalaString
    val scalarType2 = ScalaInt

    val maybeInputTypes = List(tupleType1, tupleType2, scalarType1, scalarType2).map(Some(_))

    when(collectiveTypeInferrer.inferScalar(eqOptionTreeList(maybeInputTypes))).thenReturn(Some(ScalaAny))

    compositeCollectiveTypeInferrer.infer(maybeInputTypes).structure shouldBe ScalaAny.structure
  }

  test("infer when empty should return result of inferring as scalar") {
    when(collectiveTypeInferrer.inferScalar(Nil)).thenReturn(Some(ScalaAny))

    compositeCollectiveTypeInferrer.infer(Nil).structure shouldBe ScalaAny.structure
  }
}
