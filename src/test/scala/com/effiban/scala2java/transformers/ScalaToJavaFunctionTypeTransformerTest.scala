package com.effiban.scala2java.transformers

import com.effiban.scala2java.UnitTestSuite

import scala.meta.Type

class ScalaToJavaFunctionTypeTransformerTest extends UnitTestSuite {

  test("transform for zero input types") {
    val typeT = Type.Name("T")

    val scalaFunctionType = Type.Function(params = Nil, res = typeT)

    val expectedJavaFunctionType = Type.Apply(tpe = Type.Name("Supplier"), args = List(typeT))

    val actualMaybeJavaFunctionType = ScalaToJavaFunctionTypeTransformer.transform(scalaFunctionType)

    actualMaybeJavaFunctionType.value.structure shouldBe expectedJavaFunctionType.structure
  }

  test("transform for one input type") {
    val typeT = Type.Name("T")
    val typeU = Type.Name("U")

    val scalaFunctionType = Type.Function(params = List(typeT), res = typeU)

    val expectedJavaFunctionType = Type.Apply(tpe = Type.Name("Function"), args = List(typeT, typeU))

    val actualMaybeJavaFunctionType = ScalaToJavaFunctionTypeTransformer.transform(scalaFunctionType)

    actualMaybeJavaFunctionType.value.structure shouldBe expectedJavaFunctionType.structure
  }

  test("transform for two input types") {
    val typeT1 = Type.Name("T1")
    val typeT2 = Type.Name("T2")
    val typeU = Type.Name("U")

    val scalaFunctionType = Type.Function(params = List(typeT1, typeT2), res = typeU)

    val expectedJavaFunctionType = Type.Apply(tpe = Type.Name("BiFunction"), args = List(typeT1, typeT2, typeU))

    val actualMaybeJavaFunctionType = ScalaToJavaFunctionTypeTransformer.transform(scalaFunctionType)

    actualMaybeJavaFunctionType.value.structure shouldBe expectedJavaFunctionType.structure
  }

  test("transform for three input types") {
    val typeT1 = Type.Name("T1")
    val typeT2 = Type.Name("T2")
    val typeT3 = Type.Name("T3")
    val typeU = Type.Name("U")

    val scalaFunctionType = Type.Function(params = List(typeT1, typeT2, typeT3), res = typeU)

    val actualMaybeJavaFunctionType = ScalaToJavaFunctionTypeTransformer.transform(scalaFunctionType)

    actualMaybeJavaFunctionType shouldBe None
  }
}
