package effiban.scala2java.transformers

import effiban.scala2java.testsuites.UnitTestSuite

import scala.meta.Mod.{Abstract, Final, Implicit, Lazy, Private, Protected, Sealed}
import scala.meta.{Mod, Name}

class ScalaToJavaModifierTransformerTest extends UnitTestSuite {

  private val TypeMappings = Table(
    ("ScalaModifier", "ExpectedMaybeJavaModifier"),
    (Private(Name.Anonymous()), Some("private")),
    (Protected(Name.Anonymous()), Some("protected")),
    (Abstract(), Some("abstract")),
    (Final(), Some("final")),
    (Sealed(), Some("sealed")),
    (Lazy(), None),
    (Implicit(), None),
  )

  forAll(TypeMappings) { (scalaModifier: Mod, expectedMaybeJavaModifier: Option[String]) =>
    test(s"transform $scalaModifier should return $expectedMaybeJavaModifier") {
      ScalaToJavaModifierTransformer.transform(scalaModifier) shouldBe expectedMaybeJavaModifier
    }
  }
}
