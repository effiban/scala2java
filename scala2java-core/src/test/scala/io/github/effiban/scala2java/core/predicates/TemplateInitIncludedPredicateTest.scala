package io.github.effiban.scala2java.core.predicates

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite

import scala.meta.{Init, Name, Type}

class TemplateInitIncludedPredicateTest extends UnitTestSuite {

  private val InitIncludedTypeNameScenarios = Table(
    ("Name", "ExpectedIncluded"),
    ("Product", false),
    ("Serializable", false),
    ("Enumeration", false),
    ("Other", true)
  )

  forAll(InitIncludedTypeNameScenarios) { (typeName: String, expectedIncluded: Boolean) =>
    test(s"The template init type '$typeName' should ${if (expectedIncluded) "be" else "not be"} included") {
      TemplateInitIncludedPredicate.apply(initWithTypeName(typeName)) shouldBe expectedIncluded
    }
  }

  private def initWithTypeName(typeName: String): Init = Init(tpe = Type.Name(typeName), name = Name.Anonymous(), argss = List(Nil))
}
