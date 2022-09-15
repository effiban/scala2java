package effiban.scala2java.traversers

import effiban.scala2java.matchers.TreeMatcher.eqTree
import effiban.scala2java.stubbers.OutputWriterStubber.doWrite
import effiban.scala2java.testsuites.UnitTestSuite
import effiban.scala2java.testtrees.TypeNames
import effiban.scala2java.transformers.ScalaToJavaFunctionTypeTransformer

import scala.meta.Type

class TypeFunctionTraverserImplTest extends UnitTestSuite {

  private val typeApplyTraverser = mock[TypeApplyTraverser]
  private val scalaToJavaFunctionTypeTransformer = mock[ScalaToJavaFunctionTypeTransformer]

  private val typeFunctionTraverser = new TypeFunctionTraverserImpl(typeApplyTraverser, scalaToJavaFunctionTypeTransformer)

  test("traverse() when corresponding function type is a native Java type") {
    val inputType = TypeNames.Int
    val resultType = TypeNames.String

    val scalaFunctionType = Type.Function(params = List(inputType), res = resultType)
    val expectedJavaFunctionType = Type.Apply(Type.Name("Function"), List(inputType, resultType))

    when(scalaToJavaFunctionTypeTransformer.transform(eqTree(scalaFunctionType))).thenReturn(expectedJavaFunctionType)

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

    when(scalaToJavaFunctionTypeTransformer.transform(eqTree(scalaFunctionType))).thenReturn(expectedJavaFunctionType)

    doWrite("Function3<T1, T2, T3, String>").when(typeApplyTraverser).traverse(eqTree(expectedJavaFunctionType))

    typeFunctionTraverser.traverse(scalaFunctionType)

    outputWriter.toString shouldBe
      "/* Requires JOOL (import org.jooq.lambda.function.Function3) */Function3<T1, T2, T3, String>"
  }
}
