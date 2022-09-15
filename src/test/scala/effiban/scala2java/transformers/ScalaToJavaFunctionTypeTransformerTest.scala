package effiban.scala2java.transformers


import effiban.scala2java.testsuites.UnitTestSuite

import scala.meta.Type

class ScalaToJavaFunctionTypeTransformerTest extends UnitTestSuite {

  test("transform for zero input types should return a Supplier") {
    val typeT = Type.Name("T")

    val scalaFunctionType = Type.Function(params = Nil, res = typeT)

    val expectedJavaFunctionType = Type.Apply(tpe = Type.Name("Supplier"), args = List(typeT))

    val actualJavaFunctionType = ScalaToJavaFunctionTypeTransformer.transform(scalaFunctionType)

    actualJavaFunctionType.structure shouldBe expectedJavaFunctionType.structure
  }

  test("transform for one input type should return a Function") {
    val typeT = Type.Name("T")
    val typeU = Type.Name("U")

    val scalaFunctionType = Type.Function(params = List(typeT), res = typeU)

    val expectedJavaFunctionType = Type.Apply(tpe = Type.Name("Function"), args = List(typeT, typeU))

    val actualJavaFunctionType = ScalaToJavaFunctionTypeTransformer.transform(scalaFunctionType)

    actualJavaFunctionType.structure shouldBe expectedJavaFunctionType.structure
  }

  test("transform for two input types should return a BiFunction") {
    val typeT1 = Type.Name("T1")
    val typeT2 = Type.Name("T2")
    val typeU = Type.Name("U")

    val scalaFunctionType = Type.Function(params = List(typeT1, typeT2), res = typeU)

    val expectedJavaFunctionType = Type.Apply(tpe = Type.Name("BiFunction"), args = List(typeT1, typeT2, typeU))

    val actualJavaFunctionType = ScalaToJavaFunctionTypeTransformer.transform(scalaFunctionType)

    actualJavaFunctionType.structure shouldBe expectedJavaFunctionType.structure
  }

  test("transform for three input types should return a Function3 (JOOL)") {
    val typeT1 = Type.Name("T1")
    val typeT2 = Type.Name("T2")
    val typeT3 = Type.Name("T3")
    val typeU = Type.Name("U")

    val scalaFunctionType = Type.Function(params = List(typeT1, typeT2, typeT3), res = typeU)

    val expectedJavaFunctionType = Type.Apply(tpe = Type.Name("Function3"), args = List(typeT1, typeT2, typeT3, typeU))

    val actualMaybeJavaFunctionType = ScalaToJavaFunctionTypeTransformer.transform(scalaFunctionType)

    actualMaybeJavaFunctionType.structure shouldBe expectedJavaFunctionType.structure
  }
}
