package io.github.effiban.scala2java.core.cleanup

import io.github.effiban.scala2java.core.cleanup.IsTemplateAncestorUsedByCtorInvocation.apply
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.testtrees.{PrimaryCtors, Selfs}

import scala.meta.Term.NewAnonymous
import scala.meta.{Defn, Template, Term, XtensionQuasiquoteInit, XtensionQuasiquoteTerm, XtensionQuasiquoteType}

class IsTemplateAncestorUsedByCtorInvocationTest extends UnitTestSuite {

  test("apply for class calling superclass ctor. should return true") {
    val cls =
      Defn.Class(
        mods = Nil,
        name = t"A",
        tparams = Nil,
        ctor = PrimaryCtors.Empty,
        templ = Template(
          early = Nil,
          inits = List(init"B"),
          self = Selfs.Empty,
          stats = List(
            Term.Apply(Term.Name("super"), List(q"1", q"2"))
          )
        )
      )

    apply(cls.templ, t"B") shouldBe true
  }

  test("apply for class which does not call superclass ctor. should return false") {
    val cls =
      q"""
      class A extends B {
        val x = 3
      }
      """

    apply(cls.templ, t"B") shouldBe false
  }

  test("apply for object calling superclass ctor. should return true") {
    val obj =
      Defn.Object(
        mods = Nil,
        name = q"A",
        templ = Template(
          early = Nil,
          inits = List(init"B"),
          self = Selfs.Empty,
          stats = List(
            Term.Apply(Term.Name("super"), List(q"1", q"2"))
          )
        )
      )

    apply(obj.templ, t"B") shouldBe true
  }

  test("apply for object which does not call superclass ctor. should return false") {
    val obj =
      q"""
      object A extends B {
        val x = 3
      }
      """

    apply(obj.templ, t"B") shouldBe false
  }

  test("apply for trait should return false") {
    val aTrait =
      q"""
      trait A extends B {
        val x = 3
      }
      """

    apply(aTrait.templ, t"B") shouldBe false
  }

  test("apply for anonymous class calling superclass ctor. should return true") {
    val newAnonymous =
      NewAnonymous(
        templ = Template(
          early = Nil,
          inits = List(init"B"),
          self = Selfs.Empty,
          stats = List(
            Term.Apply(Term.Name("super"), List(q"1", q"2"))
          )
        )
      )

    apply(newAnonymous.templ, t"B") shouldBe true
  }

  test("apply for anonymous class which does not call superclass ctor. should return false") {
    val newAnonymous =
      q"""
      new A with B {
        val x = 3
      }
      """

    apply(newAnonymous.templ, t"B") shouldBe false
  }

}
