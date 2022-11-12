package io.github.effiban.scala2java.core.predicates

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite

import scala.meta.{Init, Name, Type}

class CoreTemplateInitExcludedPredicateTest extends UnitTestSuite {

  private val ExcludedTypeNameScenarios = Table(
    ("Name", "ExpectedExcluded"),
    ("Product", true),
    ("Serializable", true),
    ("Enumeration", true),
    ("Other", false)
  )

  forAll(ExcludedTypeNameScenarios) { (typeName: String, expectedExcluded: Boolean) =>
    test(s"The template init type '$typeName' should ${if (!expectedExcluded) "not"} be excluded") {
      CoreTemplateInitExcludedPredicate.apply(initWithTypeName(typeName)) shouldBe expectedExcluded
    }
  }

  private def initWithTypeName(typeName: String): Init = Init(tpe = Type.Name(typeName), name = Name.Anonymous(), argss = List(Nil))
}
