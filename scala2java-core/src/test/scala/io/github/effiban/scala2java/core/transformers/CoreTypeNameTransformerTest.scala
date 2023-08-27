package io.github.effiban.scala2java.core.transformers

import io.github.effiban.scala2java.core.entities.TypeSelects.ScalaUnit
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite

import scala.meta.Type

class CoreTypeNameTransformerTest extends UnitTestSuite {

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
    (ScalaUnit.name.value, "void"),
    ("Seq", "List"),
    ("Vector", "List"),
    ("Option", "Optional"),
    ("Future", "CompletableFuture")
  )

  forAll(TypeMappings) { (scalaType: String, expectedJavaType: String) =>
    test(s"transform $scalaType should return $expectedJavaType") {
      CoreTypeNameTransformer.transform(Type.Name(scalaType)).structure shouldBe Type.Name(expectedJavaType).structure
    }
  }
}
