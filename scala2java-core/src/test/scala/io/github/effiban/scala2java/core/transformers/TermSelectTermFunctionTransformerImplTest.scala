package io.github.effiban.scala2java.core.transformers

import io.github.effiban.scala2java.core.entities.TermNames
import io.github.effiban.scala2java.core.entities.TypeSelects.JavaRunnable
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.typeinference.FunctionTypeInferrer
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.{XtensionQuasiquoteTerm, XtensionQuasiquoteType}

class TermSelectTermFunctionTransformerImplTest extends UnitTestSuite {

  private val functionTypeInferrer = mock[FunctionTypeInferrer]
  private val functionTypeTransformer = mock[FunctionTypeTransformer]
  private val treeTransformer = mock[TreeTransformer]

  private val termSelectTermFunctionTransformer = new TermSelectTermFunctionTransformerImpl(
    functionTypeInferrer,
    functionTypeTransformer,
    treeTransformer
  )

  test("transform() for a lambda of type '() => scala.Unit' and the method 'apply', should return 'java.lang.Runnable.run'") {
    val termFunction = q"""() => print("bla")"""
    val expectedTransformedTermFunction = q"""() => System.out.print("bla")"""
    val expectedTypeFunction = t"() => scala.Unit"
    val expectedResult = q"""((() => System.out.print("bla")): java.lang.Runnable).run"""

    doReturn(expectedTransformedTermFunction).when(treeTransformer).transform(eqTree(termFunction))
    when(functionTypeInferrer.infer(eqTree(termFunction))).thenReturn(t"() => scala.Unit")
    when(functionTypeTransformer.transform(eqTree(expectedTypeFunction))).thenReturn(JavaRunnable)

    termSelectTermFunctionTransformer.transform(termFunction, TermNames.Apply).structure shouldBe expectedResult.structure
  }

  test("transform() for a lambda of type '() => scala.Int' and the method 'apply', should return 'java.util.function.Supplier.get'") {
    val termFunction = q"""() => 3"""
    val expectedTransformedTermFunction = q"""() => 33"""
    val expectedTypeFunction = t"() => scala.Int"
    val expectedResult = q"""((() => 33): java.util.function.Supplier[java.lang.Integer]).get"""

    doReturn(expectedTransformedTermFunction).when(treeTransformer).transform(eqTree(termFunction))
    when(functionTypeInferrer.infer(eqTree(termFunction))).thenReturn(t"() => scala.Int")
    when(functionTypeTransformer.transform(eqTree(expectedTypeFunction))).thenReturn(t"java.util.function.Supplier[java.lang.Integer]")

    termSelectTermFunctionTransformer.transform(termFunction, TermNames.Apply).structure shouldBe expectedResult.structure
  }

  test("transform() for a lambda of type 'scala.Int => scala.Unit' and the method 'apply', should return 'java.util.function.Consumer.accept'") {
    val termFunction = q"""(x: scala.Int) => print(x)"""
    val expectedTransformedTermFunction = q"""(x: int) => System.out.print(x)"""
    val expectedTypeFunction = t"scala.Int => scala.Unit"
    val expectedResult = q"""(((x: int) => System.out.print(x)): java.util.function.Consumer[java.lang.Integer]).accept"""

    doReturn(expectedTransformedTermFunction).when(treeTransformer).transform(eqTree(termFunction))
    when(functionTypeInferrer.infer(eqTree(termFunction))).thenReturn(expectedTypeFunction)
    when(functionTypeTransformer.transform(eqTree(expectedTypeFunction))).thenReturn(t"java.util.function.Consumer[java.lang.Integer]")

    termSelectTermFunctionTransformer.transform(termFunction, TermNames.Apply).structure shouldBe expectedResult.structure
  }

  test("transform() for a lambda of type 'scala.Int => scala.Unit' and the method 'andThen', should return 'java.util.function.Consumer.andThen'") {
    val termFunction = q"""(x: scala.Int) => print(x)"""
    val expectedTransformedTermFunction = q"""(x: int) => System.out.print(x)"""
    val expectedTypeFunction = t"scala.Int => scala.Unit"
    val expectedResult = q"""(((x: int) => System.out.print(x)): java.util.function.Consumer[java.lang.Integer]).andThen"""

    doReturn(expectedTransformedTermFunction).when(treeTransformer).transform(eqTree(termFunction))
    when(functionTypeInferrer.infer(eqTree(termFunction))).thenReturn(expectedTypeFunction)
    when(functionTypeTransformer.transform(eqTree(expectedTypeFunction))).thenReturn(t"java.util.function.Consumer[java.lang.Integer]")

    termSelectTermFunctionTransformer.transform(termFunction, TermNames.AndThen).structure shouldBe expectedResult.structure
  }

  test("transform() for a lambda of type '(scala.Int, scala.String) => scala.Unit' and the method 'apply', should return 'java.util.function.BiConsumer.accept'") {
    val termFunction = q"""(x: scala.Int, y: scala.String) => print(x + y)"""
    val expectedTransformedTermFunction = q"""(x: int, y: java.lang.String) => System.out.print(x + y)"""
    val expectedTypeFunction = t"(scala.Int, scala.String) => scala.Unit"
    val expectedResult =
      q"""(((x: int, y: java.lang.String) => System.out.print(x + y)): 
         java.util.function.BiConsumer[java.lang.Integer, java.lang.String]).accept"""

    doReturn(expectedTransformedTermFunction).when(treeTransformer).transform(eqTree(termFunction))
    when(functionTypeInferrer.infer(eqTree(termFunction))).thenReturn(expectedTypeFunction)
    when(functionTypeTransformer.transform(eqTree(expectedTypeFunction))).thenReturn(t"java.util.function.BiConsumer[java.lang.Integer, java.lang.String]")

    termSelectTermFunctionTransformer.transform(termFunction, TermNames.Apply).structure shouldBe expectedResult.structure
  }

  test("transform() for a lambda of type 'scala.Int => scala.String' and the method 'apply', should return 'java.util.function.Function.apply'") {
    val termFunction = q"""(x: scala.Int) => x.toString"""
    val expectedTransformedTermFunction = q"""(x: int) => x.toString()"""
    val expectedTypeFunction = t"scala.Int => scala.String"
    val expectedResult = q"""(((x: int) => x.toString()): java.util.function.Function[java.lang.Integer, java.lang.String]).apply"""

    doReturn(expectedTransformedTermFunction).when(treeTransformer).transform(eqTree(termFunction))
    when(functionTypeInferrer.infer(eqTree(termFunction))).thenReturn(expectedTypeFunction)
    when(functionTypeTransformer.transform(eqTree(expectedTypeFunction))).thenReturn(t"java.util.function.Function[java.lang.Integer, java.lang.String]")

    termSelectTermFunctionTransformer.transform(termFunction, TermNames.Apply).structure shouldBe expectedResult.structure
  }

  test("transform() for a lambda of type 'scala.Int => scala.String' and the method 'andThen', should return 'java.util.function.Function.andThen'") {
    val termFunction = q"""(x: scala.Int) => x.toString"""
    val expectedTransformedTermFunction = q"""(x: int) => x.toString()"""
    val expectedTypeFunction = t"scala.Int => scala.String"
    val expectedResult = q"""(((x: int) => x.toString()): java.util.function.Function[java.lang.Integer, java.lang.String]).andThen"""

    doReturn(expectedTransformedTermFunction).when(treeTransformer).transform(eqTree(termFunction))
    when(functionTypeInferrer.infer(eqTree(termFunction))).thenReturn(expectedTypeFunction)
    when(functionTypeTransformer.transform(eqTree(expectedTypeFunction))).thenReturn(t"java.util.function.Function[java.lang.Integer, java.lang.String]")

    termSelectTermFunctionTransformer.transform(termFunction, TermNames.AndThen).structure shouldBe expectedResult.structure
  }
}
