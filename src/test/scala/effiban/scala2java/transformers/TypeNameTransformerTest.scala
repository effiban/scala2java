package effiban.scala2java.transformers

import effiban.scala2java.testsuites.UnitTestSuite

import scala.meta.Type

class TypeNameTransformerTest extends UnitTestSuite {

  private val TypeMappings = Table(
    ("ScalaType", "ExpectedJavaType"),
    ("Any", "Object"),
    ("AnyRef", "Object"),
    ("Boolean", "boolean"),
    ("Byte", "byte"),
    ("Short", "short"),
    ("Int", "int"),
    ("Long", "long"),
    ("Float", "float"),
    ("Double", "double"),
    ("Unit", "void"),
    ("Seq", "List"),
    ("Vector", "List"),
    ("Option", "Optional")
  )

  forAll(TypeMappings) { (scalaType: String, expectedJavaType: String) =>
    test(s"transform $scalaType should return $expectedJavaType") {
      TypeNameTransformer.transform(Type.Name(scalaType)) shouldBe expectedJavaType
    }
  }
}
