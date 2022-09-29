package effiban.scala2java.traversers

import effiban.scala2java.testsuites.UnitTestSuite
import effiban.scala2java.transformers.TypeNameTransformer

import scala.meta.Type

class TypeNameTraverserImplTest extends UnitTestSuite {

  private val typeNameTransformer = mock[TypeNameTransformer]

  private val typeNameTraverser = new TypeNameTraverserImpl(typeNameTransformer)

  test("traverse") {
    val scalaTypeName = Type.Name("Option")
    val javaTypeName = "Optional"

    when(typeNameTransformer.transform(scalaTypeName)).thenReturn(javaTypeName)

    typeNameTraverser.traverse(scalaTypeName)

    outputWriter.toString shouldBe javaTypeName
  }
}
