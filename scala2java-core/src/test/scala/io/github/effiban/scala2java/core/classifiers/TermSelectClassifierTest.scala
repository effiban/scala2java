package io.github.effiban.scala2java.core.classifiers

import io.github.effiban.scala2java.core.classifiers.TermSelectClassifier._
import io.github.effiban.scala2java.core.entities.TermSelects._
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite

import scala.meta.{Term, XtensionQuasiquoteTerm}

class TermSelectClassifierTest extends UnitTestSuite {
  private val JavaStreamLikeScenarios = Table(
    ("Term.Select", "ExpectedResult"),
    (ScalaLazyList, true),
    (ScalaStream, true),
    (ScalaList, false),
    (ScalaSet, false)
  )

  private val JavaListLikeScenarios = Table(
    ("Term.Select", "ExpectedResult"),
    (ScalaSeq, true),
    (ScalaLinearSeq, true),
    (ScalaIndexedSeq, true),
    (ScalaArraySeq, true),
    (ScalaList, true),
    (ScalaVector, true),
    (ScalaListSet, false),
    (ScalaLazyList, false)
  )

  private val JavaSetLikeScenarios = Table(
    ("Term.Select", "ExpectedResult"),
    (ScalaSet, true),
    (ScalaHashSet, true),
    (ScalaListSet, false),
    (ScalaSortedSet, false),
    (ScalaTreeSet, false)
  )

  private val JavaMapLikeScenarios = Table(
    ("Term.Select", "ExpectedResult"),
    (ScalaMap, true),
    (ScalaHashMap, true),
    (ScalaListMap, false),
    (ScalaSortedMap, false),
    (ScalaTreeMap, false)
  )

  private val ObjectsHasApplyMethodScenarios = Table(
    ("Object", "ExpectedHasEmptyMethod"),
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
    (ScalaNil, false),
    (ScalaNone, false),
    (q"foo.bar", false)
  )

  private val ObjectsHasEmptyMethodScenarios = Table(
    ("Object", "ExpectedHasEmptyMethod"),
    (ScalaArray, true),
    (ScalaOption, true),
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
    (ScalaFuture, false),
    (ScalaTry, false),
    (ScalaRange, false),
    (q"foo.bar", false)
  )

  private val SupportsNoArgInvocationScenarios = Table(
    ("Term.Select", "ExpectedHasNoArgInvocation"),
    (ScalaPrintln, true),
    (ScalaPrint, false),
    (ScalaList, false),
    (ScalaMap, false)
  )

  forAll(JavaStreamLikeScenarios) { (termSelect: Term.Select, expectedResult: Boolean) =>
    test(s"isJavaStreamLike() for '$termSelect' should return $expectedResult") {
      isJavaStreamLike(termSelect) shouldBe expectedResult
    }
  }

  forAll(JavaListLikeScenarios) { (termSelect: Term.Select, expectedResult: Boolean) =>
    test(s"isJavaListLike() for '$termSelect' should return $expectedResult") {
      isJavaListLike(termSelect) shouldBe expectedResult
    }
  }

  forAll(JavaSetLikeScenarios) { (termSelect: Term.Select, expectedResult: Boolean) =>
    test(s"isJavaSetLike() for '$termSelect' should return $expectedResult") {
      isJavaSetLike(termSelect) shouldBe expectedResult
    }
  }

  forAll(JavaMapLikeScenarios) { (termSelect: Term.Select, expectedResult: Boolean) =>
    test(s"isJavaMapLike() for '$termSelect' should return $expectedResult") {
      isJavaMapLike(termSelect) shouldBe expectedResult
    }
  }

  forAll(ObjectsHasApplyMethodScenarios) { (termSelect: Term.Select, expectedResult: Boolean) =>
    test(s"hasApplyMethod() for '$termSelect' should return $expectedResult") {
      hasApplyMethod(termSelect) shouldBe expectedResult
    }
  }

  forAll(ObjectsHasEmptyMethodScenarios) { (termSelect: Term.Select, expectedResult: Boolean) =>
    test(s"hasEmptyMethod() for '$termSelect' should return $expectedResult") {
      hasEmptyMethod(termSelect) shouldBe expectedResult
    }
  }

  forAll(SupportsNoArgInvocationScenarios) { (termSelect: Term.Select, expectedResult: Boolean) =>
    test(s"supportsNoArgInvocation() for '$termSelect' should return $expectedResult") {
      supportsNoArgInvocation(termSelect) shouldBe expectedResult
    }
  }

}
