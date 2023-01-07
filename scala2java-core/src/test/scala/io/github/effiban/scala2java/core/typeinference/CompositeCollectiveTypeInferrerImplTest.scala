package io.github.effiban.scala2java.core.typeinference

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.testtrees.TypeNames
import io.github.effiban.scala2java.core.testtrees.TypeNames.ScalaAny
import io.github.effiban.scala2java.test.utils.matchers.CombinedMatchers.{eqOptionTreeList, eqTreeList}

import scala.meta.XtensionQuasiquoteType

class CompositeCollectiveTypeInferrerImplTest extends UnitTestSuite {

  private val collectiveTypeInferrer = mock[CollectiveTypeInferrer]

  private val compositeCollectiveTypeInferrer = new CompositeCollectiveTypeInferrerImpl(collectiveTypeInferrer)

  test("infer when all types are scalars and have a collective type, should return it") {
    val maybeTypes = List(TypeNames.Int, TypeNames.Int).map(Some(_))

    when(collectiveTypeInferrer.inferScalar(eqOptionTreeList(maybeTypes))).thenReturn(Some(TypeNames.Int))

    compositeCollectiveTypeInferrer.infer(maybeTypes).structure shouldBe TypeNames.Int.structure
  }

  test("infer when all types are scalars and don't have a collective type, should return Any") {
    val maybeTypes = List(TypeNames.Int, TypeNames.String).map(Some(_))

    when(collectiveTypeInferrer.inferScalar(eqOptionTreeList(maybeTypes))).thenReturn(None)

    compositeCollectiveTypeInferrer.infer(maybeTypes).structure shouldBe ScalaAny.structure
  }

  test("infer when all types are tuples and have a collective type, should return it") {
    val tupleType = t"(String, Int)"
    val tupleTypes = List(tupleType, tupleType)

    when(collectiveTypeInferrer.inferTuple(eqTreeList(tupleTypes))).thenReturn(tupleType)

    compositeCollectiveTypeInferrer.infer(tupleTypes.map(Some(_))).structure shouldBe tupleType.structure
  }

  test("infer when all types are tuples and do not have a collective type, should return a tuple of Any-s") {
    val tupleType1 = t"(String, Int)"
    val tupleType2 = t"(Short, Long)"
    val tupleTypeAny = t"(Any, Any)"

    val inputTupleTypes = List(tupleType1, tupleType2)

    when(collectiveTypeInferrer.inferTuple(eqTreeList(inputTupleTypes))).thenReturn(tupleTypeAny)

    compositeCollectiveTypeInferrer.infer(inputTupleTypes.map(Some(_))).structure shouldBe tupleTypeAny.structure
  }

  test("infer when some types are tuples and some scalars, should return result of inferring as scalars") {
    val tupleType1 = t"(String, Int)"
    val tupleType2 = t"(Short, Long)"
    val scalarType1 = t"String"
    val scalarType2 = t"Int"

    val maybeInputTypes = List(tupleType1, tupleType2, scalarType1, scalarType2).map(Some(_))

    when(collectiveTypeInferrer.inferScalar(eqOptionTreeList(maybeInputTypes))).thenReturn(Some(ScalaAny))

    compositeCollectiveTypeInferrer.infer(maybeInputTypes).structure shouldBe ScalaAny.structure
  }
}
