package io.github.effiban.scala2java.core.reflection

import io.github.effiban.scala2java.core.entities.ScalaReflectedEntities.RuntimeMirror
import io.github.effiban.scala2java.core.reflection.ScalaReflectionUtils._
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite

import scala.meta.{Term, Type, XtensionQuasiquoteTerm, XtensionQuasiquoteType}
import scala.reflect.runtime.universe.TypeName

class ScalaReflectionUtilsTest extends UnitTestSuite {

  test("classSymbolOf(Type.Select) for an existing top-level class should return the corresponding symbol") {
    val tpe = t"scala.collection.immutable.List"

    classSymbolOf(tpe).value.fullName shouldBe "scala.collection.immutable.List"
  }

  test("classSymbolOf(Type.Project) for an existing inner class of an object should return the corresponding symbol") {
    val tpe = t"scala.collection.immutable.ArraySeq#ofRef"

    classSymbolOf(tpe).value.fullName shouldBe "scala.collection.immutable.ArraySeq.ofRef"
  }

  test("classSymbolOf(Type.Project) for an existing inner class of a trait should return the corresponding symbol") {
    val tpe = t"scala.collection.Iterator#GroupedIterator"

    classSymbolOf(tpe).value.fullName shouldBe "scala.collection.Iterator.GroupedIterator"
  }

  test("classSymbolOf(Type.Apply(Type.Select)) for an existing top-level class should return the corresponding symbol") {
    val tpe = t"scala.collection.immutable.List[scala.Int]"

    classSymbolOf(tpe).value.fullName shouldBe "scala.collection.immutable.List"
  }

  test("classSymbolOf(List[Member]) for an existing top-level class in a package should return the corresponding symbol") {
    val pkg = q"package scala.collection.immutable"
    val cls = q"class List"

    classSymbolOf(List(pkg, cls)).value.fullName shouldBe "scala.collection.immutable.List"
  }

  test("classSymbolOf(List[Member]) for an existing class type alias in a package should return the corresponding class symbol") {
    val pkg = q"package scala"
    val cls = q"class List"

    classSymbolOf(List(pkg, cls)).value.fullName shouldBe "scala.collection.immutable.List"
  }

  test("classSymbolOf(List[Member]) for an existing top-level trait in a package should return the corresponding symbol") {
    val pkg = q"package scala.collection.immutable"
    val theTrait = q"trait Seq"

    classSymbolOf(List(pkg, theTrait)).value.fullName shouldBe "scala.collection.immutable.Seq"
  }

  test("classSymbolOf(List[Member]) for a non-existing class in an existing package should return None") {
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

  test("findAndDealiasAsScalaMetaTermRef() when found by dealiasing in scala package object") {
    findAndDealiasAsScalaMetaTermRef(q"scala", q"List").value.structure shouldBe
      q"scala.collection.immutable.List".structure
  }

  test("findAndDealiasAsScalaMetaTermRef() when found by dealiasing in Predef") {
    findAndDealiasAsScalaMetaTermRef(q"scala.Predef", q"Map").value.structure shouldBe
      q"scala.collection.immutable.Map".structure
  }

  test("isTermMemberOf(Symbol, Term.Name) when true") {
    isTermMemberOf(RuntimeMirror.staticModule("scala.collection.immutable.List"), Term.Name("empty")) shouldBe true
  }

  test("isTermMemberOf(Symbol, Term.Name) when false") {
    isTermMemberOf(RuntimeMirror.staticModule("scala.collection.immutable.List"), Term.Name("bla")) shouldBe false
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

  test("isNonTrivialEmptyType() for a type which exists and is empty, should return true") {
    isNonTrivialEmptyType(t"io.github.effiban.scala2java.core.reflection.ScalaReflectionUtilsTest#EmptyTrait") shouldBe true
  }

  test("isNonTrivialEmptyType() for a type which has a non-trivial parent and nothing else, should return false") {
    isNonTrivialEmptyType(t"io.github.effiban.scala2java.core.reflection.ScalaReflectionUtilsTest#ClassWithNonTrivialParentOnly") shouldBe false
  }

  test("isNonTrivialEmptyType() for a type which has a non-default constructor and nothing else, should return false") {
    isNonTrivialEmptyType(t"io.github.effiban.scala2java.core.reflection.ScalaReflectionUtilsTest#ClassWithNonDefaultCtorOnly") shouldBe false
  }

  test("isNonTrivialEmptyType() for a type which has no parents and default ctor. but has data members, should return false") {
    isNonTrivialEmptyType(t"io.github.effiban.scala2java.core.reflection.ScalaReflectionUtilsTest#ClassWithDataMembersOnly") shouldBe false
  }

  test("isNonTrivialEmptyType() for a type which has no parents and default ctor. but has methods, should return false") {
    isNonTrivialEmptyType(t"io.github.effiban.scala2java.core.reflection.ScalaReflectionUtilsTest#ClassWithMethodsOnly") shouldBe false
  }

  test("isNonTrivialEmptyType() for a type which has a constructor and members, should return false") {
    isNonTrivialEmptyType(t"scala.concurrent.duration.FiniteDuration") shouldBe false
  }

  test("isNonTrivialEmptyType() for 'Serializable', should return false") {
    isNonTrivialEmptyType(t"java.io.Serializable") shouldBe false
  }

  trait EmptyTrait

  class ClassWithNonTrivialParentOnly extends EmptyTrait

  class ClassWithNonDefaultCtorOnly(x: Int)

  class ClassWithDataMembersOnly {
    val x: Int = 3
  }

  class ClassWithMethodsOnly {
    def foo(x: Int): Int = x + 1
  }
}
