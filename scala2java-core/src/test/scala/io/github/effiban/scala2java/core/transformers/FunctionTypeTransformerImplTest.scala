package io.github.effiban.scala2java.core.transformers

import io.github.effiban.scala2java.core.entities.TypeSelects
import io.github.effiban.scala2java.core.entities.TypeSelects.{JavaBiConsumer, JavaBiFunction, JavaConsumer, JavaRunnable, JavaSupplier, ScalaUnit}
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchersSugar.any

import scala.meta.{Type, XtensionQuasiquoteType}


class FunctionTypeTransformerImplTest extends UnitTestSuite {

  private val treeTransformer = mock[TreeTransformer]

  private val functionTypeTransformer = new FunctionTypeTransformerImpl(treeTransformer)

  test("transform for zero input types and a scala.Unit result should return a Runnable") {
    val scalaFunctionType = Type.Function(params = Nil, res = ScalaUnit)
    val expectedJavaFunctionType = JavaRunnable

    doReturn(t"void").when(treeTransformer).transform(eqTree(ScalaUnit))

    val actualJavaFunctionType = functionTypeTransformer.transform(scalaFunctionType)

    actualJavaFunctionType.structure shouldBe expectedJavaFunctionType.structure
  }

  test("transform for a scala.Unit input type and a scala.Unit result should return a Runnable") {
    val scalaFunctionType = Type.Function(params = List(ScalaUnit), res = ScalaUnit)
    val expectedJavaFunctionType = JavaRunnable

    doReturn(t"void").when(treeTransformer).transform(eqTree(ScalaUnit))

    val actualJavaFunctionType = functionTypeTransformer.transform(scalaFunctionType)

    actualJavaFunctionType.structure shouldBe expectedJavaFunctionType.structure
  }

  test("transform for zero input types and a non-scala.Unit result should return a java.util.function.Supplier") {
    val scalaResType = t"ScalaRes"
    val javaResType = t"JavaRes"

    val scalaFunctionType = Type.Function(params = Nil, res = scalaResType)
    val expectedJavaFunctionType = Type.Apply(tpe = JavaSupplier, args = List(javaResType))

    doReturn(javaResType).when(treeTransformer).transform(eqTree(scalaResType))

    val actualJavaFunctionType = functionTypeTransformer.transform(scalaFunctionType)

    actualJavaFunctionType.structure shouldBe expectedJavaFunctionType.structure
  }

  test("transform for one input type and a scala.Unit result should return a java.util.Function.Consumer") {
    val scalaInputType = t"ScalaIn"
    val javaInputType = t"JavaIn"

    val scalaFunctionType = Type.Function(params = List(scalaInputType), res = ScalaUnit)
    val expectedJavaFunctionType = Type.Apply(tpe = JavaConsumer, args = List(javaInputType))

    doAnswer((scalaType: Type) => scalaType match {
      case aType if aType.structure == scalaInputType.structure => javaInputType
      case aType if aType.structure == ScalaUnit.structure => t"void"
      case aType => aType
    }).when(treeTransformer).transform(any[Type])
    val actualJavaFunctionType = functionTypeTransformer.transform(scalaFunctionType)

    actualJavaFunctionType.structure shouldBe expectedJavaFunctionType.structure
  }

  test("transform for one input type and a non-scala.Unit result should return a java.util.Function.Function") {
    val scalaInputType = t"ScalaIn"
    val scalaResType = t"ScalaRes"

    val javaInputType = t"JavaIn"
    val javaResType = t"JavaRes"

    val scalaFunctionType = Type.Function(params = List(scalaInputType), res = scalaResType)
    val expectedJavaFunctionType = Type.Apply(tpe = TypeSelects.JavaFunction, args = List(javaInputType, javaResType))

    doAnswer((scalaType: Type) => scalaType match {
      case aType if aType.structure == scalaInputType.structure => javaInputType
      case aType if aType.structure == scalaResType.structure => javaResType
      case aType => aType
    }).when(treeTransformer).transform(any[Type])

    val actualJavaFunctionType = functionTypeTransformer.transform(scalaFunctionType)

    actualJavaFunctionType.structure shouldBe expectedJavaFunctionType.structure
  }

  test("transform for two input types and a scala.Unit result should return a java.util.Function.BiConsumer") {
    val scalaInputType1 = t"ScalaIn1"
    val scalaInputType2 = t"ScalaIn2"

    val javaInputType1 = t"JavaIn1"
    val javaInputType2 = t"JavaIn2"

    val scalaFunctionType = Type.Function(params = List(scalaInputType1, scalaInputType2), res = ScalaUnit)
    val expectedJavaFunctionType = Type.Apply(tpe = JavaBiConsumer, args = List(javaInputType1, javaInputType2))

    doAnswer((scalaType: Type) => scalaType match {
      case aType if aType.structure == scalaInputType1.structure => javaInputType1
      case aType if aType.structure == scalaInputType2.structure => javaInputType2
      case aType if aType.structure == ScalaUnit.structure => t"void"
      case aType => aType
    }).when(treeTransformer).transform(any[Type])

    val actualJavaFunctionType = functionTypeTransformer.transform(scalaFunctionType)

    actualJavaFunctionType.structure shouldBe expectedJavaFunctionType.structure
  }

  test("transform for two input types and a non-scala.Unit result should return a BiFunction") {
    val scalaInputType1 = t"ScalaIn1"
    val scalaInputType2 = t"ScalaIn2"
    val scalaResType = t"ScalaRes"

    val javaInputType1 = t"JavaIn1"
    val javaInputType2 = t"JavaIn2"
    val javaResType = t"JavaRes"

    val scalaFunctionType = Type.Function(params = List(scalaInputType1, scalaInputType2), res = scalaResType)
    val expectedJavaFunctionType = Type.Apply(tpe = JavaBiFunction, args = List(javaInputType1, javaInputType2, javaResType))

    doAnswer((scalaType: Type) => scalaType match {
      case aType if aType.structure == scalaInputType1.structure => javaInputType1
      case aType if aType.structure == scalaInputType2.structure => javaInputType2
      case aType if aType.structure == scalaResType.structure => javaResType
      case aType => aType
    }).when(treeTransformer).transform(any[Type])

    val actualJavaFunctionType = functionTypeTransformer.transform(scalaFunctionType)

    actualJavaFunctionType.structure shouldBe expectedJavaFunctionType.structure
  }

  test("transform for three input types should return a Function3 (JOOL)") {
    val scalaInputType1 = t"ScalaIn1"
    val scalaInputType2 = t"ScalaIn2"
    val scalaInputType3 = t"ScalaIn3"
    val scalaResType = t"ScalaRes"

    val javaInputType1 = t"JavaIn1"
    val javaInputType2 = t"JavaIn2"
    val javaInputType3 = t"JavaIn3"
    val javaResType = t"JavaRes"

    val scalaFunctionType = Type.Function(params = List(scalaInputType1, scalaInputType2, scalaInputType3), res = scalaResType)
    val expectedJavaFunctionType = Type.Apply(
      tpe = t"org.jooq.lambda.function.Function3",
      args = List(javaInputType1, javaInputType2, javaInputType3, javaResType)
    )

    doAnswer((scalaType: Type) => scalaType match {
      case aType if aType.structure == scalaInputType1.structure => javaInputType1
      case aType if aType.structure == scalaInputType2.structure => javaInputType2
      case aType if aType.structure == scalaInputType3.structure => javaInputType3
      case aType if aType.structure == scalaResType.structure => javaResType
      case aType => aType
    }).when(treeTransformer).transform(any[Type])

    val actualMaybeJavaFunctionType = functionTypeTransformer.transform(scalaFunctionType)

    actualMaybeJavaFunctionType.structure shouldBe expectedJavaFunctionType.structure
  }
}
