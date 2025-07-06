package io.github.effiban.scala2java.core.cleanup

import io.github.effiban.scala2java.core.reflection.ScalaReflectionLookup
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.{XtensionQuasiquoteTerm, XtensionQuasiquoteType}

class IsTemplateAncestorUsedByOverrideImplTest extends UnitTestSuite {

  private val scalaReflectionLookup = mock[ScalaReflectionLookup]

  private val isTemplateAncestorUsedByOverride = new IsTemplateAncestorUsedByOverrideImpl(scalaReflectionLookup)
  import isTemplateAncestorUsedByOverride.apply

  test("apply for type overriding parent declaration of a Decl.Var, should return true") {
    val cls =
      q"""
      class A extends ParentClass {
        override var x: Int
      }
      """

    when(scalaReflectionLookup.isTermMemberOf(eqTree(t"ParentClass"), eqTree(q"x"))).thenReturn(true)

    apply(cls.templ, t"ParentClass") shouldBe true
  }

  test("apply for type overriding parent definition of a Defn.Var, should return true") {
    val cls =
      q"""
      class A extends ParentClass {
        override var x: Int = 4
      }
      """

    when(scalaReflectionLookup.isTermMemberOf(eqTree(t"ParentClass"), eqTree(q"x"))).thenReturn(true)

    apply(cls.templ, t"ParentClass") shouldBe true
  }

  test("apply for type overriding a parent Decl.Def, should return true") {
    val cls =
      q"""
      class A extends ParentClass {
        override def foo: Int
      }
      """

    when(scalaReflectionLookup.isTermMemberOf(eqTree(t"ParentClass"), eqTree(q"foo"))).thenReturn(true)

    apply(cls.templ, t"ParentClass") shouldBe true
  }

  test("apply for type overriding parent Defn.Def, should return true") {
    val cls =
      q"""
      class A extends ParentClass {
        override def foo: Int = 5
      }
      """

    when(scalaReflectionLookup.isTermMemberOf(eqTree(t"ParentClass"), eqTree(q"foo"))).thenReturn(true)

    apply(cls.templ, t"ParentClass") shouldBe true
  }

  test("apply for type which has a member that does not override, should return false") {
    val cls =
      q"""
      class A extends ParentClass {
        def bar: Int = 5
      }
      """

    when(scalaReflectionLookup.isTermMemberOf(eqTree(t"ParentClass"), eqTree(q"bar"))).thenReturn(false)

    apply(cls.templ, t"ParentClass") shouldBe false
  }

  test("apply for type which does not have any members, should return false") {
    val cls =
      q"""
      class A extends ParentClass
      """

    apply(cls.templ, t"ParentClass") shouldBe false
  }
}
