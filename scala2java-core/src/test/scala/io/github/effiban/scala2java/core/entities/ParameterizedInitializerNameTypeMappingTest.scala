package io.github.effiban.scala2java.core.entities

import io.github.effiban.scala2java.core.entities.ParameterizedInitializerNameTypeMapping.typeInitializedBy
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.testtrees.TermNames

import scala.meta.{Term, Type, XtensionQuasiquoteTerm}

class ParameterizedInitializerNameTypeMappingTest extends UnitTestSuite {

  private val ValidScenarios = Table(
    ("Name", "ExpectedType"),
    (TermNames.ScalaRange, TypeSelects.ScalaRange),
    (TermNames.ScalaOption, TypeSelects.ScalaOption),
    (TermNames.ScalaSome, TypeSelects.ScalaSome),
    (TermNames.ScalaRight, TypeSelects.ScalaRight),
    (TermNames.ScalaLeft, TypeSelects.ScalaLeft),
    (TermNames.Try, TypeSelects.ScalaTry),
    (TermNames.ScalaSuccess, TypeSelects.ScalaSuccess),
    (TermNames.ScalaFailure, TypeSelects.ScalaFailure),
    (TermNames.Future, TypeSelects.ScalaFuture),
    (TermNames.Stream, TypeSelects.ScalaStream),
    (TermNames.ScalaArray, TypeSelects.ScalaArray),
    (TermNames.List, TypeSelects.ScalaList),
    (TermNames.ScalaVector, TypeSelects.ScalaVector),
    (TermNames.Seq, TypeSelects.ScalaSeq),
    (TermNames.Set, TypeSelects.ScalaSet),
    (TermNames.Map, TypeSelects.ScalaMap)
  )

  private val InvalidScenarios = Table(
    "Name",
    q"foo",
    q"bar"
  )

  forAll(ValidScenarios) { (name: Term.Name, expectedType: Type.Ref) =>
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
