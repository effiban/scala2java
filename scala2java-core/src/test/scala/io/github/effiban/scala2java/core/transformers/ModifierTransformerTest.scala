package io.github.effiban.scala2java.core.transformers

import io.github.effiban.scala2java.core.entities.JavaModifier
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite

import scala.meta.Mod.{Abstract, Final, Implicit, Lazy, Private, Protected, Sealed}
import scala.meta.{Mod, Name}

class ModifierTransformerTest extends UnitTestSuite {

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
      ModifierTransformer.transform(scalaModifier) shouldBe expectedMaybeJavaModifier
    }
  }
}
