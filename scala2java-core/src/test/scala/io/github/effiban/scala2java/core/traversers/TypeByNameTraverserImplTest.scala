package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.transformers.TypeByNameToSupplierTypeTransformer
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.Type

class TypeByNameTraverserImplTest extends UnitTestSuite {

  private val typeApplyTraverser = mock[TypeApplyTraverser]
  private val typeByNameToSupplierTypeTransformer = mock[TypeByNameToSupplierTypeTransformer]

  private val typeByNameTraverser = new TypeByNameTraverserImpl(typeApplyTraverser, typeByNameToSupplierTypeTransformer)

  test("traverse") {
    val tpe = Type.Name("T")
    val typeByName = Type.ByName(tpe)

    val expectedSupplierType = Type.Apply(Type.Name("Supplier"), List(tpe))

    when(typeByNameToSupplierTypeTransformer.transform(eqTree(typeByName))).thenReturn(expectedSupplierType)

    typeByNameTraverser.traverse(typeByName)

    verify(typeApplyTraverser).traverse(eqTree(expectedSupplierType))
  }

}
