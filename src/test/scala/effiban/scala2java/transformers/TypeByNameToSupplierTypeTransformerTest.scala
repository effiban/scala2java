package effiban.scala2java.transformers


import effiban.scala2java.testsuites.UnitTestSuite

import scala.meta.Type

class TypeByNameToSupplierTypeTransformerTest extends UnitTestSuite {

  test("transform") {
    val typeName = Type.Name("MyType")
    val typeByName = Type.ByName(typeName)

    val expectedSupplierType = Type.Apply(Type.Name("Supplier"), List(typeName))

    val actualSupplierType = TypeByNameToSupplierTypeTransformer.transform(typeByName)

    actualSupplierType.structure shouldBe expectedSupplierType.structure
  }

}