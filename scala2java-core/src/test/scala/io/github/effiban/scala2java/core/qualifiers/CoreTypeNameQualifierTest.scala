package io.github.effiban.scala2java.core.qualifiers

import io.github.effiban.scala2java.core.entities.TypeSelects.{ScalaAny, ScalaUnit}
import io.github.effiban.scala2java.core.qualifiers.CoreTypeNameQualifier.qualify
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite

import scala.meta.{Type, XtensionQuasiquoteType}

class CoreTypeNameQualifierTest extends UnitTestSuite {

  private final val PositiveScenarios = Table(
    ("Type", "QualifiedType"),
    (t"Any", ScalaAny),
    (t"AnyRef", t"scala.AnyRef"),
    (t"Boolean", t"scala.Boolean"),
    (t"Byte", t"scala.Byte"),
    (t"Char", t"scala.Char"),
    (t"Short", t"scala.Short"),
    (t"Int", t"scala.Int"),
    (t"Long", t"scala.Long"),
    (t"Float", t"scala.Float"),
    (t"Double", t"scala.Double"),
    (ScalaUnit.name, ScalaUnit),
    (t"Seq", t"scala.collection.immutable.Seq"),
    (t"Vector", t"scala.collection.immutable.Vector"),
    (t"List", t"scala.collection.immutable.List"),
    (t"Set", t"scala.collection.immutable.Set"),
    (t"Map", t"scala.collection.immutable.Map"),
    (t"Option", t"scala.Option"),
    (t"Future", t"scala.concurrent.Future")
  )

  forAll(PositiveScenarios) { (tpe: Type.Name, expectedQualifiedType: Type.Select) =>
    test(s"qualified type of $tpe should be $expectedQualifiedType") {
      qualify(tpe).value.structure shouldBe expectedQualifiedType.structure
    }
  }

  test("qualify() when unmapped should return None") {
    qualify(t"Bla") shouldBe None
  }
}
