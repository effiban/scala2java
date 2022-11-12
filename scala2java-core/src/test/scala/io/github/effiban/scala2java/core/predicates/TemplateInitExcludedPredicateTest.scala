package io.github.effiban.scala2java.core.predicates

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite

import scala.meta.{Init, Name, Type}

class TemplateInitExcludedPredicateTest extends UnitTestSuite {

  private val ExcludedTypeNameScenarios = Table(
    ("Name", "ExpectedExcluded"),
    ("Product", true),
    ("Serializable", true),
    ("Enumeration", true),
    ("Other", false)
  )

  forAll(ExcludedTypeNameScenarios) { (typeName: String, expectedExcluded: Boolean) =>
    test(s"The template init type '$typeName' should ${if (expectedExcluded) "be" else "not be"} excluded") {
      TemplateInitExcludedPredicate.apply(initWithTypeName(typeName)) shouldBe expectedExcluded
    }
  }

  private def initWithTypeName(typeName: String): Init = Init(tpe = Type.Name(typeName), name = Name.Anonymous(), argss = List(Nil))
}
