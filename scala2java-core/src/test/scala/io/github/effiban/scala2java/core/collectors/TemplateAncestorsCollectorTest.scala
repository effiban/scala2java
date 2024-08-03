package io.github.effiban.scala2java.core.collectors

import io.github.effiban.scala2java.core.collectors.TemplateAncestorsCollector.collect
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
}
