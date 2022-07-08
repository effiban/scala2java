package effiban.scala2java.traversers

import effiban.scala2java.UnitTestSuite
import effiban.scala2java.matchers.TreeMatcher.eqTree
import effiban.scala2java.stubbers.OutputWriterStubber.doWrite
import effiban.scala2java.testtrees.TypeNames
import effiban.scala2java.transformers.ScalaToJavaFunctionTypeTransformer

import scala.meta.Type

class TypeFunctionTraverserImplTest extends UnitTestSuite {

  private val typeApplyTraverser = mock[TypeApplyTraverser]
  private val scalaToJavaFunctionTypeTransformer = mock[ScalaToJavaFunctionTypeTransformer]

  private val typeFunctionTraverser = new TypeFunctionTraverserImpl(typeApplyTraverser, scalaToJavaFunctionTypeTransformer)

  test("traverse() when transformation is supported") {
    val inputType = TypeNames.Int
    val resultType = TypeNames.String

    val scalaFunctionType = Type.Function(params = List(inputType), res = resultType)
    val expectedJavaFunctionType = Type.Apply(Type.Name("Function"), List(inputType, resultType))

    when(scalaToJavaFunctionTypeTransformer.transform(eqTree(scalaFunctionType))).thenReturn(Some(expectedJavaFunctionType))

    doWrite("Function<Int, String>").when(typeApplyTraverser).traverse(eqTree(expectedJavaFunctionType))

    typeFunctionTraverser.traverse(scalaFunctionType)

    outputWriter.toString shouldBe "Function<Int, String>"
  }

  test("traverse() when transformation is unsupported") {
    val inType1 = Type.Name("T1")
    val inType2 = Type.Name("T2")
    val inType3 = Type.Name("T3")
    val resultType = TypeNames.String

    val scalaFunctionType = Type.Function(params = List(inType1, inType2, inType3), res = resultType)

    when(scalaToJavaFunctionTypeTransformer.transform(eqTree(scalaFunctionType))).thenReturn(None)

    typeFunctionTraverser.traverse(scalaFunctionType)

    outputWriter.toString shouldBe "/* (T1, T2, T3) => String */"

    verifyNoMoreInteractions(typeApplyTraverser)
  }
}
