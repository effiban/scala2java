package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.transformers.TypeByNameToSupplierTypeTransformer
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.XtensionQuasiquoteType

class TypeByNameTraverserImplTest extends UnitTestSuite {

  private val typeApplyTraverser = mock[TypeApplyTraverser]
  private val typeByNameToSupplierTypeTransformer = mock[TypeByNameToSupplierTypeTransformer]

  private val typeByNameTraverser = new TypeByNameTraverserImpl(typeApplyTraverser, typeByNameToSupplierTypeTransformer)

  test("traverse") {
    val tpe = t"T"
    val typeByName = t"=> T"

    val expectedSupplierType = t"Supplier[T]"
    val expectedTraversedSupplierType = t"Supplier[U]"

    when(typeByNameToSupplierTypeTransformer.transform(eqTree(typeByName))).thenReturn(expectedSupplierType)
    doReturn(expectedTraversedSupplierType).when(typeApplyTraverser).traverse(eqTree(expectedSupplierType))

    typeByNameTraverser.traverse(typeByName).structure shouldBe expectedTraversedSupplierType.structure
  }
}
