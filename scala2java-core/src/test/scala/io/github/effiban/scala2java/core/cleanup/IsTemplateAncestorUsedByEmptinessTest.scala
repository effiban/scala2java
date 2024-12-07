package io.github.effiban.scala2java.core.cleanup

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.cleanup.IsTemplateAncestorUsedByEmptiness.apply

import scala.meta.{XtensionQuasiquoteTerm, XtensionQuasiquoteType}

class IsTemplateAncestorUsedByEmptinessTest extends UnitTestSuite {

  test("apply for class extending empty class should return true") {
    val cls =
      q"""
      class A extends EmptyClass {
        val x = 3
      }
      """

    apply(cls.templ, t"io.github.effiban.scala2java.core.cleanup.IsTemplateAncestorUsedByEmptinessTest#EmptyClass") shouldBe true
  }

  test("apply for class extending empty trait should return true") {
    val cls =
      q"""
      class A extends EmptyTrait {
        val x = 3
      }
      """

    apply(cls.templ, t"io.github.effiban.scala2java.core.cleanup.IsTemplateAncestorUsedByEmptinessTest#EmptyTrait") shouldBe true
  }

  test("apply for class extending non-empty class should return false") {
    val cls =
      q"""
      class A extends NonEmptyClass {
        val x = 3
      }
      """

    apply(cls.templ, t"io.github.effiban.scala2java.core.cleanup.IsTemplateAncestorUsedByEmptinessTest#NonEmptyClass") shouldBe false
  }

  test("apply for class extending non-empty trait should return false") {
    val cls =
      q"""
      class A extends NonEmptyTrait {
        val x = 3
      }
      """

    apply(cls.templ, t"io.github.effiban.scala2java.core.cleanup.IsTemplateAncestorUsedByEmptinessTest#NonEmptyTrait") shouldBe false
  }

  test("apply for object extending empty class should return true") {
    val obj =
      q"""
      object A extends EmptyClass {
        val x = 3
      }
      """

    apply(obj.templ, t"io.github.effiban.scala2java.core.cleanup.IsTemplateAncestorUsedByEmptinessTest#EmptyClass") shouldBe true
  }

  test("apply for object extending empty trait should return true") {
    val obj =
      q"""
      object A extends EmptyTrait {
        val x = 3
      }
      """

    apply(obj.templ, t"io.github.effiban.scala2java.core.cleanup.IsTemplateAncestorUsedByEmptinessTest#EmptyTrait") shouldBe true
  }

  test("apply for object extending non-empty class should return false") {
    val obj =
      q"""
      object A extends NonEmptyClass {
        val x = 3
      }
      """

    apply(obj.templ, t"io.github.effiban.scala2java.core.cleanup.IsTemplateAncestorUsedByEmptinessTest#NonEmptyClass") shouldBe false
  }

  test("apply for object extending non-empty trait should return false") {
    val obj =
      q"""
      object A extends NonEmptyTrait {
        val x = 3
      }
      """

    apply(obj.templ, t"io.github.effiban.scala2java.core.cleanup.IsTemplateAncestorUsedByEmptinessTest#NonEmptyTrait") shouldBe false
  }

  test("apply for trait extending empty trait should return true") {
    val aTrait =
      q"""
      trait A extends EmptyTrait {
        val x = 3
      }
      """

    apply(aTrait.templ, t"io.github.effiban.scala2java.core.cleanup.IsTemplateAncestorUsedByEmptinessTest#EmptyTrait") shouldBe true
  }

  test("apply for trait extending non-empty trait should return false") {
    val aTrait =
      q"""
      trait A extends NonEmptyTrait {
        val x = 3
      }
      """

    apply(aTrait.templ, t"io.github.effiban.scala2java.core.cleanup.IsTemplateAncestorUsedByEmptinessTest#NonEmptyTrait") shouldBe false
  }

  test("apply for anonymous class extending empty trait should return true") {
    val newAnonymous =
      q"""
      new A with EmptyTrait {
        val x = 3
      }
      """

    apply(newAnonymous.templ, t"io.github.effiban.scala2java.core.cleanup.IsTemplateAncestorUsedByEmptinessTest#EmptyTrait") shouldBe true
  }

  test("apply for anonymous class extending non-empty trait should return false") {
    val newAnonymous =
      q"""
      new A with NonEmptyTrait {
        val x = 3
      }
      """

    apply(newAnonymous.templ, t"io.github.effiban.scala2java.core.cleanup.IsTemplateAncestorUsedByEmptinessTest#NonEmptyTrait") shouldBe false
  }

  class EmptyClass

  class NonEmptyClass {
    val xx = 5
  }

  trait EmptyTrait

  trait NonEmptyTrait {
    val yy = 6
  }
}


