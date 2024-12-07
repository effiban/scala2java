package io.github.effiban.scala2java.core.cleanup

import io.github.effiban.scala2java.core.cleanup.IsTemplateAncestorUsedByAbstractEnclosingType.apply
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite

import scala.meta.{XtensionQuasiquoteTerm, XtensionQuasiquoteType}

class IsTemplateAncestorUsedByAbstractEnclosingTypeTest extends UnitTestSuite {

  test("apply for concrete class should return false") {
    val cls =
      q"""
      class A extends B {
        val x = 3
      }
      """

    apply(cls.templ, t"B") shouldBe false
  }

  test("apply for abstract class should return true") {
    val cls =
      q"""
      abstract class A extends B {
        val x = 3
      }
      """

    apply(cls.templ, t"B") shouldBe true
  }

  test("apply for object should return false") {
    val obj =
      q"""
      object A extends B {
        val x = 3
      }
      """

    apply(obj.templ, t"B") shouldBe false
  }

  test("apply for trait should return true") {
    val aTrait =
      q"""
      trait A extends B {
        val x = 3
      }
      """

    apply(aTrait.templ, t"B") shouldBe true
  }

  test("apply for anonymous class should return true") {
    val anonClass =
      q"""
      new A with B {
        val x = 3
      }
      """

    apply(anonClass.templ, t"B") shouldBe true
  }
}
