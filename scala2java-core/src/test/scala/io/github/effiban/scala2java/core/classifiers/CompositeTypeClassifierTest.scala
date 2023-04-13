package io.github.effiban.scala2java.core.classifiers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite

import scala.meta.{Type, XtensionQuasiquoteType}

class CompositeTypeClassifierTest extends UnitTestSuite {

  private val JavaStreamLikeScenarios = Table(
    ("Type", "ExpectedResult"),
    (t"Stream", true),
    (t"Set", false)
  )

  private val JavaTypedStreamLikeScenarios = Table(
    ("Type", "ExpectedResult"),
    (t"Stream[Int]", true),
    (t"Set[Int]", false)
  )

  private val JavaListLikeScenarios = Table(
    ("Type", "ExpectedResult"),
    (t"Seq", true),
    (t"LazyList", false)
  )

  private val JavaTypedListLikeScenarios = Table(
    ("Type", "ExpectedResult"),
    (t"Seq[Int]", true),
    (t"LazyList[Int]", false)
  )

  private val JavaSetLikeScenarios = Table(
    ("Type", "ExpectedResult"),
    (t"Set", true),
    (t"TreeSet", false)
  )

  private val JavaTypedSetLikeScenarios = Table(
    ("Type", "ExpectedResult"),
    (t"Set[Int]", true),
    (t"TreeSet[Int]", false)
  )

  private val JavaMapLikeScenarios = Table(
    ("Type", "ExpectedResult"),
    (t"Map", true),
    (t"TreeMap", false)
  )

  private val JavaTypedMapLikeScenarios = Table(
    ("Type", "ExpectedResult"),
    (t"Map[String, Int]", true),
    (t"TreeMap[String, Int]", false)
  )

  private val typeNameClassifier = mock[TypeNameClassifier]
  
  private val compositeTypeClassifier = new CompositeTypeClassifierImpl(typeNameClassifier)

  
  forAll(JavaStreamLikeScenarios) { (typeName: Type.Name, expectedResult: Boolean) =>
    test(s"isJavaStreamLike() for '$typeName' should return $expectedResult") {
      when(typeNameClassifier.isJavaStreamLike(typeName)).thenReturn(expectedResult)
      compositeTypeClassifier.isJavaStreamLike(typeName) shouldBe expectedResult
    }
  }

  forAll(JavaTypedStreamLikeScenarios) { (typeApply: Type.Apply, expectedResult: Boolean) =>
    test(s"isJavaStreamLike() for '$typeApply' should return $expectedResult") {
      typeApply match {
        case Type.Apply(typeName: Type.Name, _) => when(typeNameClassifier.isJavaStreamLike(typeName)).thenReturn(expectedResult)
        case _ =>
      }
      compositeTypeClassifier.isJavaStreamLike(typeApply) shouldBe expectedResult
    }
  }

  forAll(JavaListLikeScenarios) { (typeName: Type.Name, expectedResult: Boolean) =>
    test(s"isJavaListLike() for '$typeName' should return $expectedResult") {
      when(typeNameClassifier.isJavaListLike(typeName)).thenReturn(expectedResult)
      compositeTypeClassifier.isJavaListLike(typeName) shouldBe expectedResult
    }
  }

  forAll(JavaTypedListLikeScenarios) { (typeApply: Type.Apply, expectedResult: Boolean) =>
    test(s"isJavaListLike() for '$typeApply' should return $expectedResult") {
      typeApply match {
        case Type.Apply(typeName: Type.Name, _) => when(typeNameClassifier.isJavaListLike(typeName)).thenReturn(expectedResult)
        case _ =>
      }
      compositeTypeClassifier.isJavaListLike(typeApply) shouldBe expectedResult
    }
  }

  forAll(JavaSetLikeScenarios) { (typeName: Type.Name, expectedResult: Boolean) =>
    test(s"isJavaSetLike() for '$typeName' should return $expectedResult") {
      when(typeNameClassifier.isJavaSetLike(typeName)).thenReturn(expectedResult)
      compositeTypeClassifier.isJavaSetLike(typeName) shouldBe expectedResult
    }
  }

  forAll(JavaTypedSetLikeScenarios) { (typeApply: Type.Apply, expectedResult: Boolean) =>
    test(s"isJavaSetLike() for '$typeApply' should return $expectedResult") {
      typeApply match {
        case Type.Apply(typeName: Type.Name, _) => when(typeNameClassifier.isJavaSetLike(typeName)).thenReturn(expectedResult)
        case _ =>
      }
      compositeTypeClassifier.isJavaSetLike(typeApply) shouldBe expectedResult
    }
  }

  forAll(JavaMapLikeScenarios) { (typeName: Type.Name, expectedResult: Boolean) =>
    test(s"isJavaMapLike() for '$typeName' should return $expectedResult") {
      when(typeNameClassifier.isJavaMapLike(typeName)).thenReturn(expectedResult)
      compositeTypeClassifier.isJavaMapLike(typeName) shouldBe expectedResult
    }
  }

  forAll(JavaTypedMapLikeScenarios) { (typeApply: Type.Apply, expectedResult: Boolean) =>
    test(s"isJavaMapLike() for '$typeApply' should return $expectedResult") {
      typeApply match {
        case Type.Apply(typeName: Type.Name, _) => when(typeNameClassifier.isJavaMapLike(typeName)).thenReturn(expectedResult)
        case _ =>
      }
      compositeTypeClassifier.isJavaMapLike(typeApply) shouldBe expectedResult
    }
  }
}
