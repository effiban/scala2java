package io.github.effiban.scala2java.core.classifiers

import io.github.effiban.scala2java.core.classifiers.CtorPrimaryClassifier.isDefault
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite

import scala.meta.Mod.Private
import scala.meta.{Ctor, Mod, Name, XtensionQuasiquoteInit, XtensionQuasiquoteTermParam}

class CtorPrimaryClassifierTest extends UnitTestSuite {

  private val IsDefaultScenarios = Table(
    ("Ctor.Primary", "ExpectedIsDefault"),
    (Ctor.Primary(Nil, Name.Anonymous(), Nil), true),
    (Ctor.Primary(Nil, Name.Anonymous(), List(Nil)), true),
    (Ctor.Primary(Nil, Name.Anonymous(), List(Nil, Nil)), true),
    (Ctor.Primary(List(Private(Name.Anonymous())), Name.Anonymous(), Nil), false),
    (Ctor.Primary(List(Mod.Annot(init"MyAnnot()")), Name.Anonymous(), Nil), false),
    (Ctor.Primary(Nil, Name.Anonymous(), List(List(param"x: Int"))), false),
  )

  forAll(IsDefaultScenarios) { (ctorPrimary: Ctor.Primary, expectedResult: Boolean) =>
    test(s"isDefault() for '$ctorPrimary' should return $expectedResult") {
      isDefault(ctorPrimary) shouldBe expectedResult
    }
  }
}
