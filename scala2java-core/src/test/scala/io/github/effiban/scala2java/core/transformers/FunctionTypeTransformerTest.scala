package io.github.effiban.scala2java.core.transformers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.testtrees.TypeNames
import io.github.effiban.scala2java.core.testtrees.TypeNames.{JavaBiConsumer, JavaBiFunction, JavaConsumer, JavaRunnable, JavaSupplier}

import scala.meta.Type


class FunctionTypeTransformerTest extends UnitTestSuite {

  test("transform for zero input types and a Unit result should return a Runnable") {
    val scalaFunctionType = Type.Function(params = Nil, res = TypeNames.Unit)

    val expectedJavaFunctionType = JavaRunnable

    val actualJavaFunctionType = FunctionTypeTransformer.transform(scalaFunctionType)

    actualJavaFunctionType.structure shouldBe expectedJavaFunctionType.structure
  }

  test("transform for a Unit input type and Unit result should return a Runnable") {
    val scalaFunctionType = Type.Function(params = List(TypeNames.Unit), res = TypeNames.Unit)

    val expectedJavaFunctionType = JavaRunnable

    val actualJavaFunctionType = FunctionTypeTransformer.transform(scalaFunctionType)

    actualJavaFunctionType.structure shouldBe expectedJavaFunctionType.structure
  }

  test("transform for zero input types and a non-Unit result should return a Supplier") {
    val typeT = Type.Name("T")

    val scalaFunctionType = Type.Function(params = Nil, res = typeT)

    val expectedJavaFunctionType = Type.Apply(tpe = JavaSupplier, args = List(typeT))

    val actualJavaFunctionType = FunctionTypeTransformer.transform(scalaFunctionType)

    actualJavaFunctionType.structure shouldBe expectedJavaFunctionType.structure
  }

  test("transform for one input type and a Unit result should return a Consumer") {
    val typeT = Type.Name("T")

    val scalaFunctionType = Type.Function(params = List(typeT), res = TypeNames.Unit)

    val expectedJavaFunctionType = Type.Apply(tpe = JavaConsumer, args = List(typeT))

    val actualJavaFunctionType = FunctionTypeTransformer.transform(scalaFunctionType)

    actualJavaFunctionType.structure shouldBe expectedJavaFunctionType.structure
  }

  test("transform for one input type and a non-Unit result should return a Function") {
    val typeT = Type.Name("T")
    val typeU = Type.Name("U")

    val scalaFunctionType = Type.Function(params = List(typeT), res = typeU)

    val expectedJavaFunctionType = Type.Apply(tpe = TypeNames.Function, args = List(typeT, typeU))

    val actualJavaFunctionType = FunctionTypeTransformer.transform(scalaFunctionType)

    actualJavaFunctionType.structure shouldBe expectedJavaFunctionType.structure
  }

  test("transform for two input types and a Unit result should return a BiConsumer") {
    val typeT1 = Type.Name("T1")
    val typeT2 = Type.Name("T2")

    val scalaFunctionType = Type.Function(params = List(typeT1, typeT2), res = TypeNames.Unit)

    val expectedJavaFunctionType = Type.Apply(tpe = JavaBiConsumer, args = List(typeT1, typeT2))

    val actualJavaFunctionType = FunctionTypeTransformer.transform(scalaFunctionType)

    actualJavaFunctionType.structure shouldBe expectedJavaFunctionType.structure
  }

  test("transform for two input types and a non-Unit result should return a BiFunction") {
    val typeT1 = Type.Name("T1")
    val typeT2 = Type.Name("T2")
    val typeU = Type.Name("U")

    val scalaFunctionType = Type.Function(params = List(typeT1, typeT2), res = typeU)

    val expectedJavaFunctionType = Type.Apply(tpe = JavaBiFunction, args = List(typeT1, typeT2, typeU))

    val actualJavaFunctionType = FunctionTypeTransformer.transform(scalaFunctionType)

    actualJavaFunctionType.structure shouldBe expectedJavaFunctionType.structure
  }

  test("transform for three input types should return a Function3 (JOOL)") {
    val typeT1 = Type.Name("T1")
    val typeT2 = Type.Name("T2")
    val typeT3 = Type.Name("T3")
    val typeU = Type.Name("U")

    val scalaFunctionType = Type.Function(params = List(typeT1, typeT2, typeT3), res = typeU)

    val expectedJavaFunctionType = Type.Apply(tpe = Type.Name("Function3"), args = List(typeT1, typeT2, typeT3, typeU))

    val actualMaybeJavaFunctionType = FunctionTypeTransformer.transform(scalaFunctionType)

    actualMaybeJavaFunctionType.structure shouldBe expectedJavaFunctionType.structure
  }
}
