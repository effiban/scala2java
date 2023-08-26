package io.github.effiban.scala2java.core.qualifiers

import io.github.effiban.scala2java.core.entities.TypeSelects._
import io.github.effiban.scala2java.core.qualifiers.CoreTypeNameQualifier.qualify
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite

import scala.meta.{Type, XtensionQuasiquoteType}

class CoreTypeNameQualifierTest extends UnitTestSuite {

  private final val PositiveScenarios = Table(
    ("Type", "QualifiedType"),
    (t"Any", ScalaAny),
    (t"AnyRef", t"scala.AnyRef"),
    (t"Array", ScalaArray),
    (t"Boolean", ScalaBoolean),
    (t"Byte", ScalaByte),
    (t"Char", ScalaChar),
    (t"Short", ScalaShort),
    (t"Int", ScalaInt),
    (t"Long", ScalaLong),
    (t"Float", ScalaFloat),
    (t"Double", ScalaDouble),
    (ScalaUnit.name, ScalaUnit),
    (t"Seq", t"scala.collection.immutable.Seq"),
    (t"Vector", t"scala.collection.immutable.Vector"),
    (t"List", ScalaList),
    (t"Set", t"scala.collection.immutable.Set"),
    (t"Map", t"scala.collection.immutable.Map"),
    (t"Option", ScalaOption),
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
