package io.github.effiban.scala2java.core.extensions

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.spi.Scala2JavaExtension

import scala.meta.{Term, XtensionQuasiquoteTerm}

class ExtensionApplicablePredicateTest extends UnitTestSuite {

  test("apply() when forced extension names provided and extension fully matches one, should return true") {
    val result = ExtensionApplicablePredicate.apply(
      TestExtension(),
      forcedExtensionNames = List(
        "io.github.effiban.scala2java.core.extensions.ExtensionApplicablePredicateTest.TestExtension",
        "io.github.effiban.scala2java.core.extensions.ExtensionApplicablePredicateTest.BlaBla"
      )
    )
    result shouldBe true
  }

  test("apply() when forced extension names provided and extension partially matches one, should return true") {
    ExtensionApplicablePredicate.apply(TestExtension(), forcedExtensionNames = List("TestExtension", "blabla")) shouldBe true
  }

  test("apply() when selects provided and extension matches one of them, should return true") {
    val extension = TestExtension(Set(q"aaa.bbb"))
    ExtensionApplicablePredicate.apply(extension, termSelects = Set(q"aaa.bbb", q"ccc.ddd")) shouldBe true
  }

  test("apply() when forced extension names and term selects provided and extension matches one of each, should return true") {
    val extension = TestExtension(Set(q"aaa.bbb"))
    val result = ExtensionApplicablePredicate.apply(
      extension,
      forcedExtensionNames = List("TestExtension", "blabla"),
      termSelects = Set(q"aaa.bbb", q"ccc.ddd")
    )
    result shouldBe true
  }

  test("apply() when forced extension names provided and extension does not match any, should return false") {
    ExtensionApplicablePredicate.apply(TestExtension(), forcedExtensionNames = List("blabla", "gaga")) shouldBe false
  }

  test("apply() when selects provided and extension does not match any, should return false") {
    val extension = TestExtension(Set(q"aaa.bbb"))
    ExtensionApplicablePredicate.apply(extension, termSelects = List(q"ccc.ddd", q"eee.fff")) shouldBe false
  }

  test("apply() when no forced extension names or selects provided, should return false") {
    val extension = TestExtension(Set(q"aaa.bbb"))
    ExtensionApplicablePredicate.apply(extension) shouldBe false
  }

  private case class TestExtension(termSelects: Iterable[Term.Select] = Nil) extends Scala2JavaExtension {
    override def shouldBeAppliedIfContains(termSelect: Term.Select): Boolean =
      termSelects.exists(_.structure == termSelect.structure)
  }
}
