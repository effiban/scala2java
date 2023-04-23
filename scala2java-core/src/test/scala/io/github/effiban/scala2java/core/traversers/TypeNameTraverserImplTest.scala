package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.spi.transformers.TypeNameTransformer

import scala.meta.Type

class TypeNameTraverserImplTest extends UnitTestSuite {

  private val typeNameTransformer = mock[TypeNameTransformer]

  private val typeNameTraverser = new TypeNameTraverserImpl(typeNameTransformer)

  test("traverse") {
    val scalaTypeName = Type.Name("Option")
    val javaTypeName = Type.Name("Optional")

    when(typeNameTransformer.transform(scalaTypeName)).thenReturn(javaTypeName)

    typeNameTraverser.traverse(scalaTypeName).structure shouldBe javaTypeName.structure
  }
}
