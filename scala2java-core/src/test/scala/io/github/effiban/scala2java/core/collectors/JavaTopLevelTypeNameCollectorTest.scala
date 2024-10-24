package io.github.effiban.scala2java.core.collectors

import io.github.effiban.scala2java.core.collectors.JavaTopLevelTypeNameCollector.collect
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite

import scala.meta.{XtensionQuasiquoteSource, XtensionQuasiquoteTerm, XtensionQuasiquoteType}

class JavaTopLevelTypeNameCollectorTest extends UnitTestSuite {

  test("collect when has the default package, and two top-level classes - should return their names") {
    val source =
      source"""
      class MyClass1
      class MyClass2
      """

    collect(source).structure shouldBe List(t"MyClass1", t"MyClass2").structure
  }

  test("collect when has a package and two top-level classes - should return their names") {
    val source =
      source"""
      package mypkg
      class MyClass1
      class MyClass2
      """

    collect(source).structure shouldBe List(t"MyClass1", t"MyClass2").structure
  }

  test("collect when has a top-level class and trait - should return their names") {
    val source =
      source"""
      package mypkg
      class MyClass1
      trait MyTrait1
      """

    collect(source).structure shouldBe List(t"MyClass1", t"MyTrait1").structure
  }

  test("collect when has a top-level class and object - should return their names") {
    val source =
      source"""
      package mypkg
      class MyClass1
      object MyObject1
      """

    collect(source).structure shouldBe List(t"MyClass1", q"MyObject1").structure
  }

  test("collect when has one top-level class - should return its name") {
    val source =
      source"""
      package mypkg
      class MyClass1
      """

    collect(source).structure shouldBe List(t"MyClass1").structure
  }

  test("collect when has a top-level class and nested class - should return only the top-level name") {
    val source =
      source"""
      package mypkg
      class MyClass1 {
        class MyInnerClass1
      }
      """

    collect(source).structure shouldBe List(t"MyClass1").structure
  }

  test("collect when no top-level types exists, should return empty") {
    val source =
      source"""
      package mypkg
      """

    collect(source) shouldBe Nil
  }
}
