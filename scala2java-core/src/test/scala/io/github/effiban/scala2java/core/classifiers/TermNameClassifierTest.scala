package io.github.effiban.scala2java.core.classifiers

import io.github.effiban.scala2java.core.classifiers.TermNameClassifier.{isJavaListLike, isJavaMapLike, isJavaSetLike, isJavaStreamLike}
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite

import scala.meta.Term

class TermNameClassifierTest extends UnitTestSuite {

  private val JavaStreamLikeScenarios = Table(
    ("Name", "ExpectedResult"),
    ("Stream", true),
    ("LazyList", true),
    ("List", false),
    ("Set", false)
  )

  private val JavaListLikeScenarios = Table(
    ("Name", "ExpectedResult"),
    ("Seq", true),
    ("IndexedSeq", true),
    ("LinearSeq", true),
    ("ArraySeq", true),
    ("List", true),
    ("Vector", true),
    ("ListSet", false),
    ("LazyList", false)
  )

  private val JavaSetLikeScenarios = Table(
    ("Name", "ExpectedResult"),
    ("Set", true),
    ("HashSet", true),
    ("ListSet", false),
    ("SortedSet", false),
    ("TreeSet", false)
  )

  private val JavaMapLikeScenarios = Table(
    ("Name", "ExpectedResult"),
    ("Map", true),
    ("HashMap", true),
    ("ListMap", false),
    ("SortedMap", false),
    ("TreeMap", false)
  )

  forAll(JavaStreamLikeScenarios) { (name: String, expectedResult: Boolean) =>
    test(s"isJavaStreamLike() for 'Term.Name($name)' should return $expectedResult") {
      isJavaStreamLike(Term.Name(name)) shouldBe expectedResult
    }
  }

  forAll(JavaListLikeScenarios) { (name: String, expectedResult: Boolean) =>
    test(s"isJavaListLike() for 'Term.Name($name)' should return $expectedResult") {
      isJavaListLike(Term.Name(name)) shouldBe expectedResult
    }
  }

  forAll(JavaSetLikeScenarios) { (name: String, expectedResult: Boolean) =>
    test(s"isJavaSetLike() for 'Term.Name($name)' should return $expectedResult") {
      isJavaSetLike(Term.Name(name)) shouldBe expectedResult
    }
  }

  forAll(JavaMapLikeScenarios) { (name: String, expectedResult: Boolean) =>
    test(s"isJavaMapLike() for 'Term.Name($name)' should return $expectedResult") {
      isJavaMapLike(Term.Name(name)) shouldBe expectedResult
    }
  }
}
