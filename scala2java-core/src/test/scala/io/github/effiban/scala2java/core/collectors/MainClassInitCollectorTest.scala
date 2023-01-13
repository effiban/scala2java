package io.github.effiban.scala2java.core.collectors

import io.github.effiban.scala2java.core.collectors.MainClassInitCollector.collect
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite

import scala.meta.{Source, XtensionQuasiquoteInit, XtensionQuasiquoteTerm}

class MainClassInitCollectorTest extends UnitTestSuite {

  test("collect when two main classes exists and have inits, should return first ones") {
    val source = Source(
      List(
        q"class MyClass1 extends MyParent1(arg1) with MyParent2",
        q"class MyClass2 extends MyParent3(arg3) with MyParent4"
      )
    )
    collect(source).structure shouldBe List(init"MyParent1(arg1)", init"MyParent2").structure
  }

  test("collect when one main class exists and has inits, should return them") {
    val source = Source(List(q"class MyClass extends MyParent1(arg1) with MyParent2"))
    collect(source).structure shouldBe List(init"MyParent1(arg1)", init"MyParent2").structure
  }

  test("collect when one main class exists and has no inits, should return empty") {
    val source = Source(List(q"class MyClass"))
    collect(source) shouldBe Nil
  }

  test("collect when no main class exists, should return empty") {
    val source = Source(List(q"trait MyTrait"))
    collect(source) shouldBe Nil
  }
}
