package io.github.effiban.scala2java.core.reflection

import io.github.effiban.scala2java.core.reflection.ScalaReflectionLookup.{findAsScalaMetaTypeRef, findModuleTermMemberOf, findSelfAndBaseClassesOf, isTermMemberOf}
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite

import scala.meta.{Term, Type, XtensionQuasiquoteTerm, XtensionQuasiquoteType}

class ScalaReflectionLookupTest extends UnitTestSuite {

  test("findSelfAndBaseClassesOf() should return the correct list of base classes") {
    findSelfAndBaseClassesOf(t"scala.collection.immutable.Seq").structure shouldBe
      List(
        t"scala.collection.immutable.Seq",
        t"scala.collection.immutable.SeqOps",
        t"scala.collection.Seq",
        t"scala.Equals",
        t"scala.collection.SeqOps",
        t"scala.PartialFunction",
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

  test("findModuleTermMemberOf() without alias in the scala package object") {
    findModuleTermMemberOf(q"scala", q"Option").value.structure shouldBe
      q"scala.Option".structure
  }

  test("findModuleTermMemberOf() without alias in the Predef object") {
    findModuleTermMemberOf(q"scala.Predef", q"classOf").value.structure shouldBe
      q"scala.Predef.classOf".structure
  }

  test("findModuleTermMemberOf() without alias in regular package") {
    findModuleTermMemberOf(q"scala.collection.immutable", q"List").value.structure shouldBe
      q"scala.collection.immutable.List".structure
  }

  test("findModuleTermMemberOf() when found by dealiasing in scala package object") {
    findModuleTermMemberOf(q"scala", q"List").value.structure shouldBe
      q"scala.collection.immutable.List".structure
  }

  test("findModuleTermMemberOf() when found by dealiasing in Predef") {
    findModuleTermMemberOf(q"scala.Predef", q"Map").value.structure shouldBe
      q"scala.collection.immutable.Map".structure
  }

  test("isTermMemberOf(Type.Ref, Term.Name) for a class when true") {
    isTermMemberOf(t"scala.collection.immutable.List", Term.Name("empty")) shouldBe true
  }

  test("isTermMemberOf(Type.Ref, Term.Name) for a class when false") {
    isTermMemberOf(t"scala.collection.immutable.List", Term.Name("bla")) shouldBe false
  }

  test("isTermMemberOf(Type.Ref, Term.Name) for a type def when true") {
    isTermMemberOf(t"scala.List", Term.Name("empty")) shouldBe true
  }

  test("isTermMemberOf(Type.Ref, Term.Name) for a type def when false") {
    isTermMemberOf(t"scala.List", Term.Name("bla")) shouldBe false
  }

  test("isTermMemberOf(Term.Ref, Term.Name) when true") {
    isTermMemberOf(q"scala.collection.immutable.List", Term.Name("apply")) shouldBe true
  }

  test("isTermMemberOf(Term.Ref, Term.Name) when false") {
    isTermMemberOf(q"scala.collection.immutable.List", Term.Name("bla")) shouldBe false
  }

  test("findAsScalaMetaTypeRef() when found directly") {
    findAsScalaMetaTypeRef(q"scala.collection.immutable", t"List").value.structure shouldBe
      t"scala.collection.immutable.List".structure
  }

  test("findAsScalaMetaTypeRef() when found by dealiasing") {
    findAsScalaMetaTypeRef(q"scala", t"List").value.structure shouldBe
      t"scala.collection.immutable.List".structure
  }

  test("findAsScalaMetaTypeRef() when not found") {
    findAsScalaMetaTypeRef(q"scala.collection.immutable", Type.Name("bla")) shouldBe None
  }

}
