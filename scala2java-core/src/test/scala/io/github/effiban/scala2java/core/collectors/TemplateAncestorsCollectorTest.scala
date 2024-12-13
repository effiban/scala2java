package io.github.effiban.scala2java.core.collectors

import io.github.effiban.scala2java.core.collectors.TemplateAncestorsCollector.{collect, collectToMap}
import io.github.effiban.scala2java.core.entities.TreeKeyedMap
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite

import scala.meta.{XtensionQuasiquoteTemplate, XtensionQuasiquoteType}

class TemplateAncestorsCollectorTest extends UnitTestSuite {

  test("collect for subtype of Scala type in package: scala.concurrent.Future") {
    val expectedAncestorTypeRefs = List(
      t"scala.concurrent.Future",
      t"scala.concurrent.Awaitable",
      t"java.lang.Object",
      t"scala.Any"
    )

    val actualAncestorTypeRefs = collect(
      template"""
      scala.concurrent.Future {
      }
      """
    )

    actualAncestorTypeRefs.structure shouldBe expectedAncestorTypeRefs.structure
  }

  test("collect for subtype of Scala inner type in Scala object: scala.Predef.Set") {
    val expectedAncestorTypeRefs = List(
      t"scala.collection.immutable.Set",
      t"scala.collection.immutable.SetOps",
      t"scala.collection.Set",
      t"scala.Equals",
      t"scala.collection.SetOps",
      t"scala.Function1",
      t"scala.collection.immutable.Iterable",
      t"scala.collection.Iterable",
      t"scala.collection.IterableFactoryDefaults",
      t"scala.collection.IterableOps",
      t"scala.collection.IterableOnceOps",
      t"scala.collection.IterableOnce",
      t"java.lang.Object",
      t"scala.Any"
    )

    val actualAncestorTypeRefs = collect(
      template"""
      scala.Predef.Set {
      }
      """
    )

    actualAncestorTypeRefs.structure shouldBe expectedAncestorTypeRefs.structure
  }

  test("collect for subtype of Scala type which is an alias to another type: scala.Iterable") {
    val expectedAncestorTypeRefs = List(
      t"scala.collection.Iterable",
      t"scala.collection.IterableFactoryDefaults",
      t"scala.collection.IterableOps",
      t"scala.collection.IterableOnceOps",
      t"scala.collection.IterableOnce",
      t"java.lang.Object",
      t"scala.Any"
    )

    val actualAncestorTypeRefs = collect(
      template"""
      scala.Iterable {
      }
      """
    )

    actualAncestorTypeRefs.structure shouldBe expectedAncestorTypeRefs.structure
  }

  test("collect for subtype of Java type in package: java.util.List") {
    val expectedAncestorTypeRefs = List(
      t"java.util.List",
      t"java.util.Collection",
      t"java.lang.Iterable",
      t"java.lang.Object",
      t"scala.Any"
    )

    val actualAncestorTypeRefs = collect(
      template"""
      java.util.List {
      }
      """
    )

    actualAncestorTypeRefs.structure shouldBe expectedAncestorTypeRefs.structure
  }

  test("collect for subtype of Java inner type in class: java.util.Map#Entry") {
    val expectedAncestorTypeRefs = List(
      t"java.util.Map#Entry",
      t"java.lang.Object",
      t"scala.Any"
    )

    val actualAncestorTypeRefs = collect(
      template"""
      java.util.Map#Entry {
      }
      """
    )

    actualAncestorTypeRefs.structure shouldBe expectedAncestorTypeRefs.structure
  }

  test("collectToMap for subtype of Scala type in package: scala.concurrent.Future") {
    val actualAncestorTypeRefMap = collectToMap(
      template"""
      scala.concurrent.Future[T] {
      }
      """
    )


    actualAncestorTypeRefMap.size shouldBe 1
    val (actualParentType, actualAncestorTypes) = actualAncestorTypeRefMap.head
    actualParentType.structure shouldBe t"scala.concurrent.Future".structure
    actualAncestorTypes.structure shouldBe
      List(t"scala.concurrent.Future",
        t"scala.concurrent.Awaitable",
        t"java.lang.Object",
        t"scala.Any"
      ).structure
  }

  test("collectToMap for subtype of Scala inner type in Scala object: scala.Predef.Set") {
    val actualAncestorTypeRefMap = collectToMap(
      template"""
      scala.Predef.Set {
      }
      """
    )

    actualAncestorTypeRefMap.size shouldBe 1
    val (actualParentType, actualAncestorTypes) = actualAncestorTypeRefMap.head
    actualParentType.structure shouldBe t"scala.collection.immutable.Set".structure
    actualAncestorTypes.structure shouldBe
      List(
        t"scala.collection.immutable.Set",
        t"scala.collection.immutable.SetOps",
        t"scala.collection.Set",
        t"scala.Equals",
        t"scala.collection.SetOps",
        t"scala.Function1",
        t"scala.collection.immutable.Iterable",
        t"scala.collection.Iterable",
        t"scala.collection.IterableFactoryDefaults",
        t"scala.collection.IterableOps",
        t"scala.collection.IterableOnceOps",
        t"scala.collection.IterableOnce",
        t"java.lang.Object",
        t"scala.Any"
      ).structure
  }

  test("collectToMap for subtype of Scala type which is an alias to another type: scala.Iterable") {
    val actualAncestorTypeRefMap = collectToMap(
      template"""
      scala.Iterable {
      }
      """
    )

    actualAncestorTypeRefMap.size shouldBe 1
    val (actualParentType, actualAncestorTypes) = actualAncestorTypeRefMap.head
    actualParentType.structure shouldBe t"scala.collection.Iterable".structure
    actualAncestorTypes.structure shouldBe List(
      t"scala.collection.Iterable",
      t"scala.collection.IterableFactoryDefaults",
      t"scala.collection.IterableOps",
      t"scala.collection.IterableOnceOps",
      t"scala.collection.IterableOnce",
      t"java.lang.Object",
      t"scala.Any"
    ).structure
  }

  test("collectToMap for subtype of Java type in package: java.util.List") {
    val actualAncestorTypeRefMap = collectToMap(
      template"""
      java.util.List {
      }
      """
    )

    actualAncestorTypeRefMap.size shouldBe 1
    val (actualParentType, actualAncestorTypes) = actualAncestorTypeRefMap.head
    actualParentType.structure shouldBe t"java.util.List".structure
    actualAncestorTypes.structure shouldBe
      List(
        t"java.util.List",
        t"java.util.Collection",
        t"java.lang.Iterable",
        t"java.lang.Object",
        t"scala.Any"
      ).structure
  }

  test("collectToMap for subtype of Java inner type in class: java.util.Map#Entry") {
    val actualAncestorTypeRefMap = collectToMap(
      template"""
      java.util.Map#Entry {
      }
      """
    )

    actualAncestorTypeRefMap.size shouldBe 1
    val (actualParentType, actualAncestorTypes) = actualAncestorTypeRefMap.head
    actualParentType.structure shouldBe t"java.util.Map#Entry".structure
    actualAncestorTypes.structure shouldBe List(
      t"java.util.Map#Entry",
      t"java.lang.Object",
      t"scala.Any"
    ).structure
  }

  test("collectToMap for template with two parents: scala.concurrent.Future and scala.collection.Iterable") {
    val actualAncestorTypeRefMap = collectToMap(
      template"""
      scala.concurrent.Future[T] with scala.collection.Iterable {
      }
      """
    )

    actualAncestorTypeRefMap.size shouldBe 2
    val actualParentTypes = actualAncestorTypeRefMap.keys.toList
    val (actualParentType1, actualAncestorTypes1) = (actualParentTypes.head, TreeKeyedMap(actualAncestorTypeRefMap, actualParentTypes.head))
    val (actualParentType2, actualAncestorTypes2) = (actualParentTypes(1), TreeKeyedMap(actualAncestorTypeRefMap, actualParentTypes(1)))

    actualParentType1.structure shouldBe t"scala.concurrent.Future".structure
    actualAncestorTypes1.structure shouldBe
      List(t"scala.concurrent.Future",
        t"scala.concurrent.Awaitable",
        t"java.lang.Object",
        t"scala.Any"
      ).structure

    actualParentType2.structure shouldBe t"scala.collection.Iterable".structure
    actualAncestorTypes2.structure shouldBe
      List(
        t"scala.collection.Iterable",
        t"scala.collection.IterableFactoryDefaults",
        t"scala.collection.IterableOps",
        t"scala.collection.IterableOnceOps",
        t"scala.collection.IterableOnce",
        t"java.lang.Object",
        t"scala.Any"
      ).structure
  }

  test("collectToMap for template with one parent (scala.concurrent.Future) and a 'self' (scala.collection.Iterable)") {
    val actualAncestorTypeRefMap = collectToMap(
      template"""
      scala.concurrent.Future[T]  { self: scala.collection.Iterable =>
      }
      """
    )

    actualAncestorTypeRefMap.size shouldBe 2
    val actualParentTypes = actualAncestorTypeRefMap.keys.toList
    val (actualParentType1, actualAncestorTypes1) = (actualParentTypes.head, TreeKeyedMap(actualAncestorTypeRefMap, actualParentTypes.head))
    val (actualParentType2, actualAncestorTypes2) = (actualParentTypes(1), TreeKeyedMap(actualAncestorTypeRefMap, actualParentTypes(1)))

    actualParentType1.structure shouldBe t"scala.concurrent.Future".structure
    actualAncestorTypes1.structure shouldBe
      List(t"scala.concurrent.Future",
        t"scala.concurrent.Awaitable",
        t"java.lang.Object",
        t"scala.Any"
      ).structure

    actualParentType2.structure shouldBe t"scala.collection.Iterable".structure
    actualAncestorTypes2.structure shouldBe
      List(
        t"scala.collection.Iterable",
        t"scala.collection.IterableFactoryDefaults",
        t"scala.collection.IterableOps",
        t"scala.collection.IterableOnceOps",
        t"scala.collection.IterableOnce",
        t"java.lang.Object",
        t"scala.Any"
      ).structure
  }
}
