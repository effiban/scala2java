package io.github.effiban.scala2java.core.cleanup

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.cleanup.IsTemplateAncestorUsedByOverride.apply

import scala.meta.{XtensionQuasiquoteTerm, XtensionQuasiquoteType}

class IsTemplateAncestorUsedByOverrideTest extends UnitTestSuite {

  test("apply for class overriding parent data member definition with 'override' modifier, should return true") {
    val cls =
      q"""
      class A extends ParentClass {
        override var x = 4
      }
      """

    apply(cls.templ, t"io.github.effiban.scala2java.core.cleanup.IsTemplateAncestorUsedByOverrideTest#ParentClass") shouldBe true
  }

  test("apply for class overriding parent data member definition without 'override' modifier, should return true") {
    val cls =
      q"""
      class A extends ParentClass {
        var x = 4
      }
      """

    apply(cls.templ, t"io.github.effiban.scala2java.core.cleanup.IsTemplateAncestorUsedByOverrideTest#ParentClass") shouldBe true
  }

  test("apply for class overriding parent method definition with 'override' modifier, should return true") {
    val cls =
      q"""
      class A extends ParentClass {
        override def foo = 5
      }
      """

    apply(cls.templ, t"io.github.effiban.scala2java.core.cleanup.IsTemplateAncestorUsedByOverrideTest#ParentClass") shouldBe true
  }

  test("apply for class overriding parent method definition without 'override' modifier, should return true") {
    val cls =
      q"""
      class A extends ParentClass {
        def foo = 5
      }
      """

    apply(cls.templ, t"io.github.effiban.scala2java.core.cleanup.IsTemplateAncestorUsedByOverrideTest#ParentClass") shouldBe true
  }

  test("apply for class which does not override any parent members, should return false") {
    val cls =
      q"""
      class A extends ParentClass
      """

    apply(cls.templ, t"io.github.effiban.scala2java.core.cleanup.IsTemplateAncestorUsedByOverrideTest#ParentClass") shouldBe false
  }

  test("apply for object overriding parent data member definition should return true") {
    val obj =
      q"""
      object A extends ParentClass {
        var x = 4
      }
      """

    apply(obj.templ, t"io.github.effiban.scala2java.core.cleanup.IsTemplateAncestorUsedByOverrideTest#ParentClass") shouldBe true
  }

  test("apply for object overriding parent method definition should return true") {
    val obj =
      q"""
      object A extends ParentClass {
        def foo = 5
      }
      """

    apply(obj.templ, t"io.github.effiban.scala2java.core.cleanup.IsTemplateAncestorUsedByOverrideTest#ParentClass") shouldBe true
  }

  test("apply for object which does not override any parent members, should return false") {
    val cls =
      q"""
      object A extends ParentClass
      """

    apply(cls.templ, t"io.github.effiban.scala2java.core.cleanup.IsTemplateAncestorUsedByOverrideTest#ParentClass") shouldBe false
  }

  test("apply for trait overriding parent data member declaration should return true") {
    val aTrait =
      q"""
      trait A extends ParentTrait {
        var x: Int
      }
      """

    apply(aTrait.templ, t"io.github.effiban.scala2java.core.cleanup.IsTemplateAncestorUsedByOverrideTest#ParentTrait") shouldBe true
  }

  test("apply for trait overriding parent method declaration should return true") {
    val aTrait =
      q"""
      trait A extends ParentTrait {
        def foo: Int
      }
      """

    apply(aTrait.templ, t"io.github.effiban.scala2java.core.cleanup.IsTemplateAncestorUsedByOverrideTest#ParentTrait") shouldBe true
  }

  test("apply for trait which does not override any parent members, should return false") {
    val aTrait =
      q"""
      trait A extends ParentTrait
      """

    apply(aTrait.templ, t"io.github.effiban.scala2java.core.cleanup.IsTemplateAncestorUsedByOverrideTest#ParentTrait") shouldBe false
  }

  class ParentClass {
    val x = 3
    def foo = 4
  }

  trait ParentTrait {
    val x: Any
    def foo: Any
  }
}
