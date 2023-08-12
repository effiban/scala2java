package io.github.effiban.scala2java.core.transformers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.transformers.CoreTypeSelectTransformer.transform

import scala.meta.{Type, XtensionQuasiquoteType}

class CoreTypeSelectTransformerTest extends UnitTestSuite {

  private val SupportedTypeMappings = Table(
    ("ScalaType", "ExpectedJavaType"),
    (t"scala.Any", t"Object"),
    (t"scala.AnyRef", t"Object"),
    (t"scala.Boolean", t"boolean"),
    (t"scala.Byte", t"byte"),
    (t"scala.Char", t"char"),
    (t"scala.Short", t"short"),
    (t"scala.Int", t"int"),
    (t"scala.Long", t"long"),
    (t"scala.Float", t"float"),
    (t"scala.Double", t"double"),
    (t"scala.Unit", t"void"),
    (t"scala.collection.immutable.Seq", t"java.util.List"),
    (t"scala.collection.immutable.Vector", t"java.util.List"),
    (t"scala.collection.immutable.List", t"java.util.List"),
    (t"scala.collection.immutable.Set", t"java.util.Set"),
    (t"scala.collection.immutable.Map", t"java.util.Map"),
    (t"scala.Option", t"java.util.Optional"),
    (t"scala.concurrent.Future", t"java.util.concurrent.CompletableFuture")
  )
  
  forAll(SupportedTypeMappings) { (scalaType: Type.Select, expectedJavaType: Type.Ref) =>
    test(s"transform $scalaType should return $expectedJavaType") {
      transform(scalaType).value.structure shouldBe expectedJavaType.structure
    }
  }

  test("transform() when unsupported should return None") {
    transform(t"a.b.C") shouldBe None
  }
}
