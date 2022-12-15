package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.testtrees.TypeNames
import io.github.effiban.scala2java.core.transformers.FunctionTypeTransformer
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.Type

class TypeFunctionTraverserImplTest extends UnitTestSuite {

  private val typeApplyTraverser = mock[TypeApplyTraverser]
  private val functionTypeTransformer = mock[FunctionTypeTransformer]

  private val typeFunctionTraverser = new TypeFunctionTraverserImpl(typeApplyTraverser, functionTypeTransformer)

  test("traverse() when corresponding function type is a native Java type") {
    val inputType = TypeNames.Int
    val resultType = TypeNames.String

    val scalaFunctionType = Type.Function(params = List(inputType), res = resultType)
    val expectedJavaFunctionType = Type.Apply(Type.Name("Function"), List(inputType, resultType))

    when(functionTypeTransformer.transform(eqTree(scalaFunctionType))).thenReturn(expectedJavaFunctionType)

    doWrite("Function<Int, String>").when(typeApplyTraverser).traverse(eqTree(expectedJavaFunctionType))

    typeFunctionTraverser.traverse(scalaFunctionType)

    outputWriter.toString shouldBe "Function<Int, String>"
  }

  test("traverse() when corresponding function type is a JOOL library type") {
    val inType1 = Type.Name("T1")
    val inType2 = Type.Name("T2")
    val inType3 = Type.Name("T3")
    val inParams = List(inType1, inType2, inType3)
    val resultType = TypeNames.String

    val scalaFunctionType = Type.Function(params = inParams, res = resultType)
    val expectedJavaFunctionType = Type.Apply(Type.Name("Function3"), inParams :+ resultType)

    when(functionTypeTransformer.transform(eqTree(scalaFunctionType))).thenReturn(expectedJavaFunctionType)

    doWrite("Function3<T1, T2, T3, String>").when(typeApplyTraverser).traverse(eqTree(expectedJavaFunctionType))

    typeFunctionTraverser.traverse(scalaFunctionType)

    outputWriter.toString shouldBe "Function3<T1, T2, T3, String>"
  }
}
