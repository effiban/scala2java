package io.github.effiban.scala2java.core.entities

import io.github.effiban.scala2java.core.entities.ParameterizedInitializerNameTypeMapping.typeInitializedBy
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.testtrees.{TermNames, TypeNames}

import scala.meta.{Term, Type, XtensionQuasiquoteTerm}

class ParameterizedInitializerNameTypeMappingTest extends UnitTestSuite {

  private val ValidScenarios = Table(
    ("Name", "ExpectedType"),
    (TermNames.ScalaRange, TypeNames.ScalaRange),
    (TermNames.ScalaOption, TypeNames.ScalaOption),
    (TermNames.ScalaSome, TypeNames.ScalaOption),
    (TermNames.ScalaRight, TypeNames.Either),
    (TermNames.ScalaLeft, TypeNames.Either),
    (TermNames.Try, TypeNames.Try),
    (TermNames.ScalaSuccess, TypeNames.Try),
    (TermNames.ScalaFailure, TypeNames.Try),
    (TermNames.Future, TypeNames.Future),
    (TermNames.Stream, TypeNames.Stream),
    (TermNames.ScalaArray, TypeNames.ScalaArray),
    (TermNames.List, TypeNames.List),
    (TermNames.ScalaVector, TypeNames.ScalaVector),
    (TermNames.Seq, TypeNames.Seq),
    (TermNames.Set, TypeNames.Set),
    (TermNames.Map, TypeNames.Map)
  )

  private val InvalidScenarios = Table(
    "Name",
    q"foo",
    q"bar"
  )

  forAll(ValidScenarios) { (name: Term.Name, expectedType: Type.Name) =>
    test(s"Type initialized by $name should be $expectedType") {
      typeInitializedBy(name).value.structure shouldBe expectedType.structure
    }
  }

  forAll(InvalidScenarios) { (name: Term.Name) =>
    test(s"Type initialized by $name should be None") {
      typeInitializedBy(name) shouldBe None
    }
  }
}
