package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.transformers.FunctionTypeTransformer
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.XtensionQuasiquoteType

class TypeFunctionTraverserImplTest extends UnitTestSuite {

  private val typeTraverser = mock[TypeTraverser]
  private val functionTypeTransformer = mock[FunctionTypeTransformer]

  private val typeFunctionTraverser = new TypeFunctionTraverserImpl(typeTraverser, functionTypeTransformer)

  test("traverse()") {
    val functionType = t"T1 => U1"
    val expectedTransformedFunctionType = t"Function[T1, U1]"
    val expectedTraversedFunctionType = t"Function[T2, U2]"

    when(functionTypeTransformer.transform(eqTree(functionType))).thenReturn(expectedTransformedFunctionType)
    doReturn(expectedTraversedFunctionType).when(typeTraverser).traverse(eqTree(expectedTransformedFunctionType))

    typeFunctionTraverser.traverse(functionType).structure shouldBe expectedTraversedFunctionType.structure
  }
}
