package io.github.effiban.scala2java.core.classifiers

import io.github.effiban.scala2java.core.classifiers.TypeNameClassifier._
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite

import scala.meta.Type

class TypeNameClassifierTest extends UnitTestSuite {

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
    test(s"isJavaStreamLike() for 'Type.Name($name)' should return $expectedResult") {
      isJavaStreamLike(Type.Name(name)) shouldBe expectedResult
    }
  }

  forAll(JavaListLikeScenarios) { (name: String, expectedResult: Boolean) =>
    test(s"isJavaListLike() for 'Type.Name($name)' should return $expectedResult") {
      isJavaListLike(Type.Name(name)) shouldBe expectedResult
    }
  }

  forAll(JavaSetLikeScenarios) { (name: String, expectedResult: Boolean) =>
    test(s"isJavaSetLike() for 'Type.Name($name)' should return $expectedResult") {
      isJavaSetLike(Type.Name(name)) shouldBe expectedResult
    }
  }

  forAll(JavaMapLikeScenarios) { (name: String, expectedResult: Boolean) =>
    test(s"isJavaMapLike() for 'Type.Name($name)' should return $expectedResult") {
      isJavaMapLike(Type.Name(name)) shouldBe expectedResult
    }
  }
}
