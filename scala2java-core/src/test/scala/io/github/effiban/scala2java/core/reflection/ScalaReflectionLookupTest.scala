package io.github.effiban.scala2java.core.reflection

import io.github.effiban.scala2java.core.reflection.ScalaReflectionAccess.RuntimeMirror
import io.github.effiban.scala2java.core.reflection.ScalaReflectionLookup.{findAsScalaMetaTypeRef, findModuleTermMemberOf, isTermMemberOf, selfAndBaseClassesOf}
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite

import scala.meta.{Term, Type, XtensionQuasiquoteTerm, XtensionQuasiquoteType}

class ScalaReflectionLookupTest extends UnitTestSuite {

  test("selfAndBaseClassesOf() should return the correct list of base classes") {
    val clsSymbol = RuntimeMirror.staticClass("scala.collection.immutable.Seq")
    selfAndBaseClassesOf(clsSymbol).map(_.fullName) shouldBe
      List(
        "scala.collection.immutable.Seq",
        "scala.collection.immutable.SeqOps",
        "scala.collection.Seq",
        "scala.Equals",
        "scala.collection.SeqOps",
        "scala.PartialFunction",
        "scala.Function1",
        "scala.collection.immutable.Iterable",
        "scala.collection.Iterable",
        "scala.collection.IterableFactoryDefaults",
        "scala.collection.IterableOps",
        "scala.collection.IterableOnceOps",
        "scala.collection.IterableOnce",
        "java.lang.Object",
        "scala.Any"
      )
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
