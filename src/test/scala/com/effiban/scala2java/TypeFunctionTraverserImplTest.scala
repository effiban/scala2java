package com.effiban.scala2java

import com.effiban.scala2java.matchers.TreeMatcher.eqTree
import com.effiban.scala2java.testtrees.TypeNames
import com.effiban.scala2java.transformers.ScalaToJavaFunctionTypeTransformer

import scala.meta.Type

class TypeFunctionTraverserImplTest extends UnitTestSuite {

  private val typeApplyTraverser = mock[TypeApplyTraverser]
  private val scalaToJavaFunctionTypeTransformer = mock[ScalaToJavaFunctionTypeTransformer]

  private val typeFunctionTraverser = new TypeFunctionTraverserImpl(typeApplyTraverser, scalaToJavaFunctionTypeTransformer)

  test("traverse()") {
    val inputType = TypeNames.Int
    val resultType = TypeNames.String

    val scalaFunctionType = Type.Function(params = List(inputType), res = resultType)
    val expectedJavaFunctionType = Type.Apply(Type.Name("Function"), List(inputType, resultType))

    when(scalaToJavaFunctionTypeTransformer.transform(eqTree(scalaFunctionType))).thenReturn(expectedJavaFunctionType)

    typeFunctionTraverser.traverse(scalaFunctionType)

    verify(typeApplyTraverser).traverse(eqTree(expectedJavaFunctionType))
  }

}
