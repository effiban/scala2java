package effiban.scala2java.traversers

import effiban.scala2java.testsuites.UnitTestSuite
import effiban.scala2java.transformers.ScalaToJavaTypeNameTransformer

import scala.meta.Type

class TypeNameTraverserImplTest extends UnitTestSuite {

  private val scalaToJavaTypeNameTransformer = mock[ScalaToJavaTypeNameTransformer]

  private val typeNameTraverser = new TypeNameTraverserImpl(scalaToJavaTypeNameTransformer)

  test("traverse") {
    val scalaTypeName = Type.Name("Option")
    val javaTypeName = "Optional"

    when(scalaToJavaTypeNameTransformer.transform(scalaTypeName)).thenReturn(javaTypeName)

    typeNameTraverser.traverse(scalaTypeName)

    outputWriter.toString shouldBe javaTypeName
  }
}
