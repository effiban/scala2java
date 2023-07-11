package io.github.effiban.scala2java.core.classifiers

import io.github.effiban.scala2java.core.classifiers.TraitClassifier.isEnumTypeDef
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.spi.entities.JavaScope
import io.github.effiban.scala2java.spi.entities.JavaScope.JavaScope

import scala.meta.Defn.Trait
import scala.meta.XtensionQuasiquoteTerm

class TraitClassifierTest extends UnitTestSuite {

  private val IsEnumTypeDefScenarios = Table(
    ("Trait", "JavaScope", "ExpectedResult"),
    (q"trait MyEnum extends Value", JavaScope.Enum, true),
    (q"trait MyEnum extends Value", JavaScope.Class, false),
    (q"trait MyEnum extends BlaBla", JavaScope.Enum, false),
    (q"trait MyEnum extends BlaBla", JavaScope.Class, false)
  )

  forAll(IsEnumTypeDefScenarios) { (defnTrait: Trait, javaScope: JavaScope, expectedResult: Boolean) =>
    test(s"isEnumTypeDef() for '$defnTrait' in Java scope '$javaScope' should return $expectedResult") {
      isEnumTypeDef(defnTrait, javaScope) shouldBe expectedResult
    }
  }
}
