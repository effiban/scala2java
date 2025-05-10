package io.github.effiban.scala2java.core.predicates

import io.github.effiban.scala2java.core.entities.TermSelects._
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite

import scala.meta.{Term, XtensionQuasiquoteTerm}

class TermSelectHasApplyMethodTest extends UnitTestSuite {

  private val Scenarios = Table(
    ("Term.Select", "ExpectedHasApplyMethod"),
    (ScalaArray, true),
    (ScalaRange, true),
    (ScalaOption, true),
    (ScalaSome, true),
    (ScalaRight, true),
    (ScalaLeft, true),
    (ScalaTry, true),
    (ScalaSuccess, true),
    (ScalaFailure, true),
    (ScalaStream, true),
    (ScalaLazyList, true),
    (ScalaSeq, true),
    (ScalaIndexedSeq, true),
    (ScalaLinearSeq, true),
    (ScalaArraySeq, true),
    (ScalaList, true),
    (ScalaVector, true),
    (ScalaSet, true),
    (ScalaHashSet, true),
    (ScalaSortedSet, true),
    (ScalaTreeSet, true),
    (ScalaListSet, true),
    (ScalaMap, true),
    (ScalaHashMap, true),
    (ScalaListMap, true),
    (ScalaSortedMap, true),
    (ScalaTreeMap, true),
    (ScalaNil, true),
    (ScalaNone, false),
    (q"foo.bar", false)
  )

  forAll(Scenarios) { (termSelect: Term.Select, expectedResult: Boolean) =>
    test(s"For '$termSelect' the predicate should return $expectedResult") {
      TermSelectHasApplyMethod(termSelect) shouldBe expectedResult
    }
  }
}
