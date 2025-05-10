package io.github.effiban.scala2java.core.classifiers

import io.github.effiban.scala2java.core.entities.TypeSelects.{ScalaLazyList, ScalaMap, ScalaSeq, ScalaSet, ScalaStream}
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite

import scala.meta.{Type, XtensionQuasiquoteType}

class CompositeTypeClassifierTest extends UnitTestSuite {

  private val JavaStreamLikeScenarios = Table(
    ("Type", "ExpectedResult"),
    (ScalaStream, true),
    (ScalaSet, false)
  )

  private val JavaTypedStreamLikeScenarios = Table(
    ("Type", "ExpectedResult"),
    (t"scala.collection.immutable.Stream[Int]", true),
    (t"scala.collection.immutable.Set[Int]", false)
  )

  private val JavaListLikeScenarios = Table(
    ("Type", "ExpectedResult"),
    (ScalaSeq, true),
    (ScalaLazyList, false)
  )

  private val JavaTypedListLikeScenarios = Table(
    ("Type", "ExpectedResult"),
    (t"scala.Seq[Int]", true),
    (t"scala.LazyList[Int]", false)
  )

  private val JavaSetLikeScenarios = Table(
    ("Type", "ExpectedResult"),
    (ScalaSet, true),
    (t"scala.collection.immutable.TreeSet", false)
  )

  private val JavaTypedSetLikeScenarios = Table(
    ("Type", "ExpectedResult"),
    (t"scala.collection.immutable.Set[Int]", true),
    (t"scala.collection.immutable.TreeSet[Int]", false)
  )

  private val JavaMapLikeScenarios = Table(
    ("Type", "ExpectedResult"),
    (ScalaMap, true),
    (t"scala.collection.immutable.TreeMap", false)
  )

  private val JavaTypedMapLikeScenarios = Table(
    ("Type", "ExpectedResult"),
    (t"scala.collection.immutable.Map[String, Int]", true),
    (t"scala.collection.immutable.TreeMap[String, Int]", false)
  )

  private val typeRefClassifier = mock[TypeRefClassifier]
  
  private val compositeTypeClassifier = new CompositeTypeClassifierImpl(typeRefClassifier)

  
  forAll(JavaStreamLikeScenarios) { (typeRef: Type.Ref, expectedResult: Boolean) =>
    test(s"isJavaStreamLike() for '$typeRef' should return $expectedResult") {
      when(typeRefClassifier.isJavaStreamLike(typeRef)).thenReturn(expectedResult)
      compositeTypeClassifier.isJavaStreamLike(typeRef) shouldBe expectedResult
    }
  }

  forAll(JavaTypedStreamLikeScenarios) { (typeApply: Type.Apply, expectedResult: Boolean) =>
    test(s"isJavaStreamLike() for '$typeApply' should return $expectedResult") {
      typeApply match {
        case Type.Apply(typeRef: Type.Ref, _) => when(typeRefClassifier.isJavaStreamLike(typeRef)).thenReturn(expectedResult)
        case _ =>
      }
      compositeTypeClassifier.isJavaStreamLike(typeApply) shouldBe expectedResult
    }
  }

  forAll(JavaListLikeScenarios) { (typeRef: Type.Ref, expectedResult: Boolean) =>
    test(s"isJavaListLike() for '$typeRef' should return $expectedResult") {
      when(typeRefClassifier.isJavaListLike(typeRef)).thenReturn(expectedResult)
      compositeTypeClassifier.isJavaListLike(typeRef) shouldBe expectedResult
    }
  }

  forAll(JavaTypedListLikeScenarios) { (typeApply: Type.Apply, expectedResult: Boolean) =>
    test(s"isJavaListLike() for '$typeApply' should return $expectedResult") {
      typeApply match {
        case Type.Apply(typeRef: Type.Ref, _) => when(typeRefClassifier.isJavaListLike(typeRef)).thenReturn(expectedResult)
        case _ =>
      }
      compositeTypeClassifier.isJavaListLike(typeApply) shouldBe expectedResult
    }
  }

  forAll(JavaSetLikeScenarios) { (typeRef: Type.Ref, expectedResult: Boolean) =>
    test(s"isJavaSetLike() for '$typeRef' should return $expectedResult") {
      when(typeRefClassifier.isJavaSetLike(typeRef)).thenReturn(expectedResult)
      compositeTypeClassifier.isJavaSetLike(typeRef) shouldBe expectedResult
    }
  }

  forAll(JavaTypedSetLikeScenarios) { (typeApply: Type.Apply, expectedResult: Boolean) =>
    test(s"isJavaSetLike() for '$typeApply' should return $expectedResult") {
      typeApply match {
        case Type.Apply(typeRef: Type.Ref, _) => when(typeRefClassifier.isJavaSetLike(typeRef)).thenReturn(expectedResult)
        case _ =>
      }
      compositeTypeClassifier.isJavaSetLike(typeApply) shouldBe expectedResult
    }
  }

  forAll(JavaMapLikeScenarios) { (typeRef: Type.Ref, expectedResult: Boolean) =>
    test(s"isJavaMapLike() for '$typeRef' should return $expectedResult") {
      when(typeRefClassifier.isJavaMapLike(typeRef)).thenReturn(expectedResult)
      compositeTypeClassifier.isJavaMapLike(typeRef) shouldBe expectedResult
    }
  }

  forAll(JavaTypedMapLikeScenarios) { (typeApply: Type.Apply, expectedResult: Boolean) =>
    test(s"isJavaMapLike() for '$typeApply' should return $expectedResult") {
      typeApply match {
        case Type.Apply(typeRef: Type.Ref, _) => when(typeRefClassifier.isJavaMapLike(typeRef)).thenReturn(expectedResult)
        case _ =>
      }
      compositeTypeClassifier.isJavaMapLike(typeApply) shouldBe expectedResult
    }
  }
}
