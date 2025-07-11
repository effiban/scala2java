package io.github.effiban.scala2java.core.reflection

import io.github.effiban.scala2java.core.reflection.ScalaReflectionTypeInferrer.inferScalaMetaTypeOf
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite

import scala.meta.{XtensionQuasiquoteTerm, XtensionQuasiquoteType}

class ScalaReflectionTypeInferrerTest extends UnitTestSuite {

  test("inferScalaMetaTypeOf() for 'TestObjectWithDataMembersOnly.x' should return 'scala.Int'") {
    inferScalaMetaTypeOf(q"io.github.effiban.scala2java.core.reflection.TestObjectWithDataMembersOnly", q"x").value.structure shouldBe
      t"scala.Int".structure
  }

  test("inferScalaMetaTypeOf() for 'TestClassWithDataMembersOnly.x' should return 'scala.Int'") {
    inferScalaMetaTypeOf(t"io.github.effiban.scala2java.core.reflection.TestClassWithDataMembersOnly", q"x").value.structure shouldBe
      t"scala.Int".structure
  }

  test("inferScalaMetaTypeOf() for 'TestInnerClassWithDataMembersOnly.x' should return 'scala.Int'") {
    inferScalaMetaTypeOf(t"io.github.effiban.scala2java.core.reflection.ScalaReflectionTypeInferrerTest#TestInnerClassWithDataMembersOnly", q"x").value.structure shouldBe
      t"scala.Int".structure
  }

  test("inferScalaMetaTypeOf() for 'TestClassWithDataMembersOnly.y' should return '(scala.Int, java.lang.String)'") {
    inferScalaMetaTypeOf(t"io.github.effiban.scala2java.core.reflection.TestClassWithDataMembersOnly", q"y").value.structure shouldBe
      t"(scala.Int, java.lang.String)".structure
  }

  test("inferScalaMetaTypeOf() for 'TestClassWithDataMembersOnly.z' should return '(scala.Int, scala.Long, java.lang.String) => java.lang.String'") {
    inferScalaMetaTypeOf(t"io.github.effiban.scala2java.core.reflection.TestClassWithDataMembersOnly", q"z").value.structure shouldBe
      t"(scala.Int, scala.Long, java.lang.String) => java.lang.String".structure
  }

  test("inferScalaMetaTypeOf() for 'TestClassWithDataMembersOnly.w' should return 'scala.collection.immutable.List[scala.Long]'") {
    inferScalaMetaTypeOf(t"io.github.effiban.scala2java.core.reflection.TestClassWithDataMembersOnly", q"w").value.structure shouldBe
      t"scala.collection.immutable.List[scala.Long]".structure
  }

  class TestInnerClassWithDataMembersOnly {
    val x: Int = 3
  }

}
