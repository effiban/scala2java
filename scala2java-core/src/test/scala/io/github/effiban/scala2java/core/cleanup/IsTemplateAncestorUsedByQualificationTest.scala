package io.github.effiban.scala2java.core.cleanup

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.cleanup.IsTemplateAncestorUsedByQualification.apply

import scala.meta.{XtensionQuasiquoteTerm, XtensionQuasiquoteType}

class IsTemplateAncestorUsedByQualificationTest extends UnitTestSuite {

  test("apply for class using superclass data member in class body, should return true") {
    val cls =
      q"""
      class A extends ParentClass {
        val a = super[ParentClass].x
      }
      """

    apply(cls.templ, t"io.github.effiban.scala2java.core.cleanup.IsTemplateAncestorUsedByQualificationTest#ParentClass") shouldBe true
  }

  test("apply for class using superclass data member in method body, should return true") {
    val cls =
      q"""
      class A extends ParentClass {
        def a(): Int = super[ParentClass].x
      }
      """

    apply(cls.templ, t"io.github.effiban.scala2java.core.cleanup.IsTemplateAncestorUsedByQualificationTest#ParentClass") shouldBe true
  }

  test("apply for class calling superclass method in class body, should return true") {
    val cls =
      q"""
      class A extends ParentClass {
        val a = super[ParentClass].foo(3)
      }
      """

    apply(cls.templ, t"io.github.effiban.scala2java.core.cleanup.IsTemplateAncestorUsedByQualificationTest#ParentClass") shouldBe true
  }

  test("apply for class calling superclass method in method body, should return true") {
    val cls =
      q"""
      class A extends ParentClass {
        def a() = super[ParentClass].foo(3)
      }
      """

    apply(cls.templ, t"io.github.effiban.scala2java.core.cleanup.IsTemplateAncestorUsedByQualificationTest#ParentClass") shouldBe true
  }

  test("apply for class which references other parent members only, should return false") {
    val cls =
      q"""
      class A extends ParentClass with ParentTrait {
        def a() = super[ParentTrait].bar(3)
      }
      """

    apply(cls.templ, t"io.github.effiban.scala2java.core.cleanup.IsTemplateAncestorUsedByQualificationTest#ParentClass") shouldBe false
  }

  test("apply for class which doesn't reference any parent members, should return false") {
    val cls =
      q"""
      class A extends ParentClass with ParentTrait
      """

    apply(cls.templ, t"io.github.effiban.scala2java.core.cleanup.IsTemplateAncestorUsedByQualificationTest#ParentClass") shouldBe false
  }

  test("apply for object using superclass data member, should return true") {
    val obj =
      q"""
      object A extends ParentClass {
        val a = super[ParentClass].x
      }
      """

    apply(obj.templ, t"io.github.effiban.scala2java.core.cleanup.IsTemplateAncestorUsedByQualificationTest#ParentClass") shouldBe true
  }

  test("apply for object calling superclass method, should return true") {
    val obj =
      q"""
      object A extends ParentClass {
        val a = super[ParentClass].foo(3)
      }
      """

    apply(obj.templ, t"io.github.effiban.scala2java.core.cleanup.IsTemplateAncestorUsedByQualificationTest#ParentClass") shouldBe true
  }

  test("apply for object which doesn't reference any parent members, should return false") {
    val obj =
      q"""
      object A extends ParentClass
      """

    apply(obj.templ, t"io.github.effiban.scala2java.core.cleanup.IsTemplateAncestorUsedByQualificationTest#ParentClass") shouldBe false
  }

  test("apply for trait using supertrait data member, should return true") {
    val aTrait =
      q"""
      trait A extends ParentTrait {
        val a = super[ParentTrait].x
      }
      """

    apply(aTrait.templ, t"io.github.effiban.scala2java.core.cleanup.IsTemplateAncestorUsedByQualificationTest#ParentTrait") shouldBe true
  }

  test("apply for trait calling supertrait method, should return true") {
    val aTrait =
      q"""
      trait A extends ParentTrait {
        val a = super[ParentTrait].foo(3)
      }
      """

    apply(aTrait.templ, t"io.github.effiban.scala2java.core.cleanup.IsTemplateAncestorUsedByQualificationTest#ParentTrait") shouldBe true
  }

  test("apply for trait which doesn't reference any parent members, should return false") {
    val aTrait =
      q"""
      trait A extends ParentTrait
      """

    apply(aTrait.templ, t"io.github.effiban.scala2java.core.cleanup.IsTemplateAncestorUsedByQualificationTest#ParentTrait") shouldBe false
  }

  test("apply for anonymous class using supertrait data member, should return true") {
    val anonClass =
      q"""
      new A with ParentTrait {
        val a = super[ParentTrait].x
      }
      """

    apply(anonClass.templ, t"io.github.effiban.scala2java.core.cleanup.IsTemplateAncestorUsedByQualificationTest#ParentTrait") shouldBe true
  }

  test("apply for anonymous class calling supertrait method, should return true") {
    val anonClass =
      q"""
      new A with ParentTrait {
        val a = super[ParentTrait].foo(3)
      }
      """

    apply(anonClass.templ, t"io.github.effiban.scala2java.core.cleanup.IsTemplateAncestorUsedByQualificationTest#ParentTrait") shouldBe true
  }

  test("apply for anonymous class which doesn't reference any parent members, should return false") {
    val anonClass =
      q"""
      new A with ParentTrait
      """

    apply(anonClass.templ, t"io.github.effiban.scala2java.core.cleanup.IsTemplateAncestorUsedByQualificationTest#ParentTrait") shouldBe false
  }

  class ParentClass {
    def x: Int = 3
    def foo(y: Int): Int = y + 1
  }

  trait ParentTrait {
    val z: Int
    def bar(w: Int): Int
  }
}
