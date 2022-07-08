package effiban.scala2java.traversers

import effiban.scala2java.UnitTestSuite
import effiban.scala2java.matchers.TreeMatcher.eqTree
import effiban.scala2java.transformers.TypeByNameToSupplierTypeTransformer

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
