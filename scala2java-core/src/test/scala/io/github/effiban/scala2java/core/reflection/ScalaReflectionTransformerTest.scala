package io.github.effiban.scala2java.core.reflection

import io.github.effiban.scala2java.core.reflection.ScalaReflectionAccess.RuntimeMirror
import io.github.effiban.scala2java.core.reflection.ScalaReflectionTransformer.{asScalaMetaTypeRef, classSymbolOf}
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import scala.reflect.runtime.universe.TypeName

import scala.meta.XtensionQuasiquoteType

class ScalaReflectionTransformerTest extends UnitTestSuite {

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

}
