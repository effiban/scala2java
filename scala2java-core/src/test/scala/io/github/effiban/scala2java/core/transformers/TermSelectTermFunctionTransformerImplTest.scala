package io.github.effiban.scala2java.core.transformers

import io.github.effiban.scala2java.core.entities.TermNames
import io.github.effiban.scala2java.core.entities.TypeSelects.JavaRunnable
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.typeinference.FunctionTypeInferrer
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchersSugar.any

import scala.meta.{Tree, XtensionQuasiquoteTerm, XtensionQuasiquoteType}

class TermSelectTermFunctionTransformerImplTest extends UnitTestSuite {

  private val functionTypeInferrer = mock[FunctionTypeInferrer]
  private val treeTransformer = mock[TreeTransformer]

  private val termSelectTermFunctionTransformer = new TermSelectTermFunctionTransformerImpl(
    functionTypeInferrer,
    treeTransformer
  )

  test("transform() for a lambda of type '() => scala.Unit' and the method 'apply', should return 'java.lang.Runnable.run'") {
    val termFunction = q"""() => print("bla")"""
    val expectedTransformedTermFunction = q"""() => System.out.print("bla")"""
    val expectedTypeFunction = t"() => scala.Unit"
    val expectedResult = q"""((() => System.out.print("bla")): java.lang.Runnable).run"""

    doAnswer((tree: Tree) => tree match {
      case aTree if aTree.structure == termFunction.structure => expectedTransformedTermFunction
      case aTree if aTree.structure == expectedTypeFunction.structure => JavaRunnable
      case other => other
    }).when(treeTransformer).transform(any[Tree])
    when(functionTypeInferrer.infer(eqTree(termFunction))).thenReturn(t"() => scala.Unit")

    termSelectTermFunctionTransformer.transform(termFunction, TermNames.Apply).structure shouldBe expectedResult.structure
  }

  test("transform() for a lambda of type '() => scala.Int' and the method 'apply', should return 'java.util.function.Supplier.get'") {
    val termFunction = q"""() => 3"""
    val expectedTransformedTermFunction = q"""() => 33"""
    val expectedTypeFunction = t"() => scala.Int"
    val expectedResult = q"""((() => 33): java.util.function.Supplier[java.lang.Integer]).get"""

    doAnswer((tree: Tree) => tree match {
      case aTree if aTree.structure == termFunction.structure => expectedTransformedTermFunction
      case aTree if aTree.structure == expectedTypeFunction.structure => t"java.util.function.Supplier[java.lang.Integer]"
      case other => other
    }).when(treeTransformer).transform(any[Tree])
    when(functionTypeInferrer.infer(eqTree(termFunction))).thenReturn(t"() => scala.Int")

    termSelectTermFunctionTransformer.transform(termFunction, TermNames.Apply).structure shouldBe expectedResult.structure
  }

  test("transform() for a lambda of type 'scala.Int => scala.Unit' and the method 'apply', should return 'java.util.function.Consumer.accept'") {
    val termFunction = q"""(x: scala.Int) => print(x)"""
    val expectedTransformedTermFunction = q"""(x: int) => System.out.print(x)"""
    val expectedTypeFunction = t"scala.Int => scala.Unit"
    val expectedResult = q"""(((x: int) => System.out.print(x)): java.util.function.Consumer[java.lang.Integer]).accept"""

    doAnswer((tree: Tree) => tree match {
      case aTree if aTree.structure == termFunction.structure => expectedTransformedTermFunction
      case aTree if aTree.structure == expectedTypeFunction.structure => t"java.util.function.Consumer[java.lang.Integer]"
      case other => other
    }).when(treeTransformer).transform(any[Tree])
    when(functionTypeInferrer.infer(eqTree(termFunction))).thenReturn(expectedTypeFunction)

    termSelectTermFunctionTransformer.transform(termFunction, TermNames.Apply).structure shouldBe expectedResult.structure
  }

  test("transform() for a lambda of type 'scala.Int => scala.Unit' and the method 'andThen', should return 'java.util.function.Consumer.andThen'") {
    val termFunction = q"""(x: scala.Int) => print(x)"""
    val expectedTransformedTermFunction = q"""(x: int) => System.out.print(x)"""
    val expectedTypeFunction = t"scala.Int => scala.Unit"
    val expectedResult = q"""(((x: int) => System.out.print(x)): java.util.function.Consumer[java.lang.Integer]).andThen"""

    doAnswer((tree: Tree) => tree match {
      case aTree if aTree.structure == termFunction.structure => expectedTransformedTermFunction
      case aTree if aTree.structure == expectedTypeFunction.structure => t"java.util.function.Consumer[java.lang.Integer]"
      case other => other
    }).when(treeTransformer).transform(any[Tree])
    when(functionTypeInferrer.infer(eqTree(termFunction))).thenReturn(expectedTypeFunction)

    termSelectTermFunctionTransformer.transform(termFunction, TermNames.AndThen).structure shouldBe expectedResult.structure
  }

  test("transform() for a lambda of type '(scala.Int, scala.String) => scala.Unit' and the method 'apply', should return 'java.util.function.BiConsumer.accept'") {
    val termFunction = q"""(x: scala.Int, y: scala.String) => print(x + y)"""
    val expectedTransformedTermFunction = q"""(x: int, y: java.lang.String) => System.out.print(x + y)"""
    val expectedTypeFunction = t"(scala.Int, scala.String) => scala.Unit"
    val expectedResult =
      q"""(((x: int, y: java.lang.String) => System.out.print(x + y)): 
         java.util.function.BiConsumer[java.lang.Integer, java.lang.String]).accept"""

    doAnswer((tree: Tree) => tree match {
      case aTree if aTree.structure == termFunction.structure => expectedTransformedTermFunction
      case aTree if aTree.structure == expectedTypeFunction.structure => t"java.util.function.BiConsumer[java.lang.Integer, java.lang.String]"
      case other => other
    }).when(treeTransformer).transform(any[Tree])
    when(functionTypeInferrer.infer(eqTree(termFunction))).thenReturn(expectedTypeFunction)

    termSelectTermFunctionTransformer.transform(termFunction, TermNames.Apply).structure shouldBe expectedResult.structure
  }

  test("transform() for a lambda of type 'scala.Int => scala.String' and the method 'apply', should return 'java.util.function.Function.apply'") {
    val termFunction = q"""(x: scala.Int) => x.toString"""
    val expectedTransformedTermFunction = q"""(x: int) => x.toString()"""
    val expectedTypeFunction = t"scala.Int => scala.String"
    val expectedResult = q"""(((x: int) => x.toString()): java.util.function.Function[java.lang.Integer, java.lang.String]).apply"""

    doAnswer((tree: Tree) => tree match {
      case aTree if aTree.structure == termFunction.structure => expectedTransformedTermFunction
      case aTree if aTree.structure == expectedTypeFunction.structure => t"java.util.function.Function[java.lang.Integer, java.lang.String]"
      case other => other
    }).when(treeTransformer).transform(any[Tree])
    when(functionTypeInferrer.infer(eqTree(termFunction))).thenReturn(expectedTypeFunction)

    termSelectTermFunctionTransformer.transform(termFunction, TermNames.Apply).structure shouldBe expectedResult.structure
  }

  test("transform() for a lambda of type 'scala.Int => scala.String' and the method 'andThen', should return 'java.util.function.Function.andThen'") {
    val termFunction = q"""(x: scala.Int) => x.toString"""
    val expectedTransformedTermFunction = q"""(x: int) => x.toString()"""
    val expectedTypeFunction = t"scala.Int => scala.String"
    val expectedResult = q"""(((x: int) => x.toString()): java.util.function.Function[java.lang.Integer, java.lang.String]).andThen"""

    doAnswer((tree: Tree) => tree match {
      case aTree if aTree.structure == termFunction.structure => expectedTransformedTermFunction
      case aTree if aTree.structure == expectedTypeFunction.structure => t"java.util.function.Function[java.lang.Integer, java.lang.String]"
      case other => other
    }).when(treeTransformer).transform(any[Tree])
    when(functionTypeInferrer.infer(eqTree(termFunction))).thenReturn(expectedTypeFunction)

    termSelectTermFunctionTransformer.transform(termFunction, TermNames.AndThen).structure shouldBe expectedResult.structure
  }
}
