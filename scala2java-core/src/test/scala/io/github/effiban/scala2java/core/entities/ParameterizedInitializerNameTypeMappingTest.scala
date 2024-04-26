package io.github.effiban.scala2java.core.entities

import io.github.effiban.scala2java.core.entities.ParameterizedInitializerNameTypeMapping.typeInitializedBy
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite

import scala.meta.{Term, Type, XtensionQuasiquoteTerm}

class ParameterizedInitializerNameTypeMappingTest extends UnitTestSuite {

  private val ValidScenarios = Table(
    ("Term.Select", "ExpectedType"),
    (TermSelects.ScalaRange, TypeSelects.ScalaRange),
    (TermSelects.ScalaOption, TypeSelects.ScalaOption),
    (TermSelects.ScalaSome, TypeSelects.ScalaSome),
    (TermSelects.ScalaRight, TypeSelects.ScalaRight),
    (TermSelects.ScalaLeft, TypeSelects.ScalaLeft),
    (TermSelects.ScalaTry, TypeSelects.ScalaTry),
    (TermSelects.ScalaSuccess, TypeSelects.ScalaSuccess),
    (TermSelects.ScalaFailure, TypeSelects.ScalaFailure),
    (TermSelects.ScalaFuture, TypeSelects.ScalaFuture),
    (TermSelects.ScalaStream, TypeSelects.ScalaStream),
    (TermSelects.ScalaArray, TypeSelects.ScalaArray),
    (TermSelects.ScalaList, TypeSelects.ScalaList),
    (TermSelects.ScalaVector, TypeSelects.ScalaVector),
    (TermSelects.ScalaSeq, TypeSelects.ScalaSeq),
    (TermSelects.ScalaSet, TypeSelects.ScalaSet),
    (TermSelects.ScalaMap, TypeSelects.ScalaMap)
  )

  private val InvalidScenarios = Table(
    "Term.Select",
    q"foo.foo",
    q"bar.bar"
  )

  forAll(ValidScenarios) { (termSelect: Term.Select, expectedType: Type.Ref) =>
    test(s"Type initialized by $termSelect should be $expectedType") {
      typeInitializedBy(termSelect).value.structure shouldBe expectedType.structure
    }
  }

  forAll(InvalidScenarios) { (termSelect: Term.Select) =>
    test(s"Type initialized by $termSelect should be None") {
      typeInitializedBy(termSelect) shouldBe None
    }
  }
}
