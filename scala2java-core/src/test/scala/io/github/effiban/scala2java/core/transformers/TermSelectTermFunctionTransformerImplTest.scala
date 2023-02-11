package io.github.effiban.scala2java.core.transformers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.testtrees.TermNames
import io.github.effiban.scala2java.core.testtrees.TypeNames.JavaRunnable
import io.github.effiban.scala2java.core.typeinference.FunctionTypeInferrer
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.{XtensionQuasiquoteTerm, XtensionQuasiquoteType}

class TermSelectTermFunctionTransformerImplTest extends UnitTestSuite {

  private val functionTypeInferrer = mock[FunctionTypeInferrer]
  private val functionTypeTransformer = mock[FunctionTypeTransformer]

  private val termSelectTermFunctionTransformer = new TermSelectTermFunctionTransformerImpl(functionTypeInferrer, functionTypeTransformer)

  test("transform() for a lambda of type '() => Unit' and the method 'apply', should return 'Runnable.run'") {
    val termFunction = q"""() => print("bla")"""
    val expectedTypeFunction = t"() => Unit"
    val expectedResult = q"""((() => print("bla")): Runnable).run"""

    when(functionTypeInferrer.infer(eqTree(termFunction))).thenReturn(t"() => Unit")
    when(functionTypeTransformer.transform(eqTree(expectedTypeFunction))).thenReturn(JavaRunnable)

    termSelectTermFunctionTransformer.transform(termFunction, TermNames.Apply).structure shouldBe expectedResult.structure
  }

  test("transform() for a lambda of type '() => Int' and the method 'apply', should return 'Supplier.get'") {
    val termFunction = q"""() => 3"""
    val expectedTypeFunction = t"() => Int"
    val expectedResult = q"""((() => 3): Supplier[Int]).get"""

    when(functionTypeInferrer.infer(eqTree(termFunction))).thenReturn(t"() => Int")
    when(functionTypeTransformer.transform(eqTree(expectedTypeFunction))).thenReturn(t"Supplier[Int]")

    termSelectTermFunctionTransformer.transform(termFunction, TermNames.Apply).structure shouldBe expectedResult.structure
  }

  test("transform() for a lambda of type 'Int => Unit' and the method 'apply', should return 'Consumer.accept'") {
    val termFunction = q"""(x: Int) => print(x)"""
    val expectedTypeFunction = t"Int => Unit"
    val expectedResult = q"""(((x: Int) => print(x)): Consumer[Int]).accept"""

    when(functionTypeInferrer.infer(eqTree(termFunction))).thenReturn(t"Int => Unit")
    when(functionTypeTransformer.transform(eqTree(expectedTypeFunction))).thenReturn(t"Consumer[Int]")

    termSelectTermFunctionTransformer.transform(termFunction, TermNames.Apply).structure shouldBe expectedResult.structure
  }

  test("transform() for a lambda of type 'Int => Unit' and the method 'andThen', should return 'Consumer.andThen'") {
    val termFunction = q"""(x: Int) => print(x)"""
    val expectedTypeFunction = t"Int => Unit"
    val expectedResult = q"""(((x: Int) => print(x)): Consumer[Int]).andThen"""

    when(functionTypeInferrer.infer(eqTree(termFunction))).thenReturn(t"Int => Unit")
    when(functionTypeTransformer.transform(eqTree(expectedTypeFunction))).thenReturn(t"Consumer[Int]")

    termSelectTermFunctionTransformer.transform(termFunction, TermNames.AndThen).structure shouldBe expectedResult.structure
  }

  test("transform() for a lambda of type '(Int, String) => Unit' and the method 'apply', should return 'BiConsumer.accept'") {
    val termFunction = q"""(x: Int, y: String) => print(x + y)"""
    val expectedTypeFunction = t"(Int, String) => Unit"
    val expectedResult = q"""(((x: Int, y: String) => print(x + y)): BiConsumer[Int, String]).accept"""

    when(functionTypeInferrer.infer(eqTree(termFunction))).thenReturn(t"(Int, String) => Unit")
    when(functionTypeTransformer.transform(eqTree(expectedTypeFunction))).thenReturn(t"BiConsumer[Int, String]")

    termSelectTermFunctionTransformer.transform(termFunction, TermNames.Apply).structure shouldBe expectedResult.structure
  }

  test("transform() for a lambda of type 'Int => String' and the method 'apply', should return 'Function.apply'") {
    val termFunction = q"""(x: Int) => x.toString"""
    val expectedTypeFunction = t"Int => String"
    val expectedResult = q"""(((x: Int) => x.toString): Function[Int, String]).apply"""

    when(functionTypeInferrer.infer(eqTree(termFunction))).thenReturn(t"Int => String")
    when(functionTypeTransformer.transform(eqTree(expectedTypeFunction))).thenReturn(t"Function[Int, String]")

    termSelectTermFunctionTransformer.transform(termFunction, TermNames.Apply).structure shouldBe expectedResult.structure
  }

  test("transform() for a lambda of type 'Int => String' and the method 'andThen', should return 'Function.andThen'") {
    val termFunction = q"""(x: Int) => x.toString"""
    val expectedTypeFunction = t"Int => String"
    val expectedResult = q"""(((x: Int) => x.toString): Function[Int, String]).andThen"""

    when(functionTypeInferrer.infer(eqTree(termFunction))).thenReturn(t"Int => String")
    when(functionTypeTransformer.transform(eqTree(expectedTypeFunction))).thenReturn(t"Function[Int, String]")

    termSelectTermFunctionTransformer.transform(termFunction, TermNames.AndThen).structure shouldBe expectedResult.structure
  }
}
