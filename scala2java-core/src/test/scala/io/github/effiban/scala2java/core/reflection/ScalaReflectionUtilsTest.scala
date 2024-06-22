package io.github.effiban.scala2java.core.reflection

import io.github.effiban.scala2java.core.entities.ReflectedEntities.RuntimeMirror
import io.github.effiban.scala2java.core.reflection.ScalaReflectionUtils._
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite

import scala.meta.{Term, Type, XtensionQuasiquoteTerm, XtensionQuasiquoteType}
import scala.reflect.runtime.universe.TypeName

class ScalaReflectionUtilsTest extends UnitTestSuite {

  test("classSymbolOf() for an existing top-level class in a package should return the corresponding symbol") {
    val pkg = q"package scala.collection.immutable"
    val cls = q"class List"

    classSymbolOf(List(pkg, cls)).value.fullName shouldBe "scala.collection.immutable.List"
  }

  test("classSymbolOf() for an existing class type alias in a package should return the corresponding class symbol") {
    val pkg = q"package scala"
    val cls = q"class List"

    classSymbolOf(List(pkg, cls)).value.fullName shouldBe "scala.collection.immutable.List"
  }

  test("classSymbolOf() for an existing top-level trait in a package should return the corresponding symbol") {
    val pkg = q"package scala.collection.immutable"
    val theTrait = q"trait Seq"

    classSymbolOf(List(pkg, theTrait)).value.fullName shouldBe "scala.collection.immutable.Seq"
  }

  test("classSymbolOf() for a non-existing class in an existing package should return None") {
    val pkg = q"package scala.collection.immutable"
    val cls = q"class Bla"

    classSymbolOf(List(pkg, cls)) shouldBe None
  }

  test("symbolOf() for an existing top-level class in a package should return the corresponding symbol") {
    val pkg = q"package scala.collection.immutable"
    val cls = q"class List"

    symbolOf(List(pkg, cls)).value.fullName shouldBe "scala.collection.immutable.List"
  }

  test("symbolOf() for an existing class type alias in a package should return the corresponding symbol") {
    val pkg = q"package scala"
    val cls = q"class List"

    symbolOf(List(pkg, cls)).value.fullName shouldBe "scala.List"
  }

  test("symbolOf() for an existing top-level trait in a package should return the corresponding symbol") {
    val pkg = q"package scala.collection.immutable"
    val theTrait = q"trait Seq"

    symbolOf(List(pkg, theTrait)).value.fullName shouldBe "scala.collection.immutable.Seq"
  }

  test("symbolOf() for an existing top-level object in a package should return the corresponding symbol") {
    val pkg = q"package scala.collection.immutable"
    val obj = q"object List"

    symbolOf(List(pkg, obj)).value.fullName shouldBe "scala.collection.immutable.List"
  }

  test("symbolOf() for a non-existing class in an existing package should return None") {
    val pkg = q"package scala.collection.immutable"
    val cls = q"class Bla"

    symbolOf(List(pkg, cls)) shouldBe None
  }

  test("asScalaMetaTypeRef() for an outer class should return a corresponding Type.Select") {
    val clsSymbol = RuntimeMirror.staticClass("scala.collection.immutable.List")

    asScalaMetaTypeRef(clsSymbol).value.structure shouldBe t"scala.collection.immutable.List".structure
  }

  test("asScalaMetaTypeRef() for an inner class should return a corresponding Type.Project") {
    val clsSymbol = RuntimeMirror.staticModule("scala.collection.immutable.ArraySeq")
      .typeSignature
      .decl(TypeName("ofRef"))
      .asClass

    asScalaMetaTypeRef(clsSymbol).value.structure shouldBe t"scala.collection.immutable.ArraySeq#ofRef".structure
  }

  test("baseClassesOf() should return the correct list of base classes") {
    val clsSymbol = RuntimeMirror.staticClass("scala.collection.immutable.Seq")
    baseClassesOf(clsSymbol).map(_.fullName) shouldBe
      List(
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

  test("isTermMemberOf() when true") {
    isTermMemberOf(RuntimeMirror.staticModule("scala.collection.immutable.List"), Term.Name("empty")) shouldBe true
  }

  test("isTermMemberOf() when false") {
    isTermMemberOf(RuntimeMirror.staticModule("scala.collection.immutable.List"), Term.Name("bla")) shouldBe false
  }

  test("isTypeMemberOf() when true") {
    isTypeMemberOf(RuntimeMirror.staticPackage("scala.collection.immutable"), Type.Name("List")) shouldBe true
  }

  test("isTypeMemberOf() when false") {
    isTypeMemberOf(RuntimeMirror.staticPackage("scala.collection.immutable"), Type.Name("bla")) shouldBe false
  }
}
