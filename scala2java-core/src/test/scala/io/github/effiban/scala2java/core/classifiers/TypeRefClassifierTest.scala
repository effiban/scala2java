package io.github.effiban.scala2java.core.classifiers

import io.github.effiban.scala2java.core.classifiers.TypeRefClassifier.{isJavaListLike, isJavaMapLike, isJavaSetLike, isJavaStreamLike}
import io.github.effiban.scala2java.core.entities.TypeSelects._
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite

import scala.meta.Type

class TypeRefClassifierTest extends UnitTestSuite {

  private val JavaStreamLikeScenarios = Table(
    ("Type", "ExpectedResult"),
    (ScalaStream, true),
    (ScalaLazyList, true),
    (ScalaList, false),
    (ScalaSet, false)
  )

  private val JavaListLikeScenarios = Table(
    ("Type", "ExpectedResult"),
    (ScalaSeq, true),
    (ScalaIndexedSeq, true),
    (ScalaLinearSeq, true),
    (ScalaArraySeq, true),
    (ScalaList, true),
    (ScalaVector, true),
    (ScalaListSet, false),
    (ScalaLazyList, false)
  )

  private val JavaSetLikeScenarios = Table(
    ("Type", "ExpectedResult"),
    (ScalaSet, true),
    (ScalaHashSet, true),
    (ScalaListSet, false),
    (ScalaSortedSet, false),
    (ScalaTreeSet, false)
  )

  private val JavaMapLikeScenarios = Table(
    ("Type", "ExpectedResult"),
    (ScalaMap, true),
    (ScalaHashMap, true),
    (ScalaListMap, false),
    (ScalaSortedMap, false),
    (ScalaTreeMap, false)
  )

  forAll(JavaStreamLikeScenarios) { (typeRef: Type.Ref, expectedResult: Boolean) =>
    test(s"isJavaStreamLike() for '$typeRef' should return $expectedResult") {
      isJavaStreamLike(typeRef) shouldBe expectedResult
    }
  }

  forAll(JavaListLikeScenarios) { (typeRef: Type.Ref, expectedResult: Boolean) =>
    test(s"isJavaListLike() for '$typeRef' should return $expectedResult") {
      isJavaListLike(typeRef) shouldBe expectedResult
    }
  }

  forAll(JavaSetLikeScenarios) { (typeRef: Type.Ref, expectedResult: Boolean) =>
    test(s"isJavaSetLike() for '$typeRef' should return $expectedResult") {
      isJavaSetLike(typeRef) shouldBe expectedResult
    }
  }

  forAll(JavaMapLikeScenarios) { (typeRef: Type.Ref, expectedResult: Boolean) =>
    test(s"isJavaMapLike() for '$typeRef' should return $expectedResult") {
      isJavaMapLike(typeRef) shouldBe expectedResult
    }
  }
}
