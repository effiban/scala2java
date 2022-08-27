package effiban.scala2java.transformers

import effiban.scala2java.entities.JavaModifier
import effiban.scala2java.testsuites.UnitTestSuite

import scala.meta.Mod.{Abstract, Final, Implicit, Lazy, Private, Protected, Sealed}
import scala.meta.{Mod, Name}

class ScalaToJavaModifierTransformerTest extends UnitTestSuite {

  private val TypeMappings = Table(
    ("ScalaModifier", "ExpectedMaybeJavaModifier"),
    (Private(Name.Anonymous()), Some(JavaModifier.Private)),
    (Protected(Name.Anonymous()), Some(JavaModifier.Protected)),
    (Private(Name.Indeterminate("mypkg")), None),
    (Protected(Name.Indeterminate("mypkg")), None),
    (Sealed(), Some(JavaModifier.Sealed)),
    (Abstract(), Some(JavaModifier.Abstract)),
    (Final(), Some(JavaModifier.Final)),
    (Lazy(), None),
    (Implicit(), None)
  )

  forAll(TypeMappings) { (scalaModifier: Mod, expectedMaybeJavaModifier: Option[JavaModifier]) =>
    test(s"transform $scalaModifier should return $expectedMaybeJavaModifier") {
      ScalaToJavaModifierTransformer.transform(scalaModifier) shouldBe expectedMaybeJavaModifier
    }
  }
}
