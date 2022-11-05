package io.github.effiban.scala2java.core.transformers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite

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
    ("Array", "Object[]"),
    ("Seq", "List"),
    ("Vector", "List"),
    ("Option", "Optional"),
    ("Future", "CompletableFuture")
  )

  forAll(TypeMappings) { (scalaType: String, expectedJavaType: String) =>
    test(s"transform $scalaType should return $expectedJavaType") {
      TypeNameTransformer.transform(Type.Name(scalaType)) shouldBe expectedJavaType
    }
  }
}
