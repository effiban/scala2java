package io.github.effiban.scala2java.core.reflection

import io.github.effiban.scala2java.core.reflection.ScalaReflectionTypeInferrer.inferScalaMetaTypeOf
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite

import scala.meta.{Type, XtensionQuasiquoteTerm, XtensionQuasiquoteType}

class ScalaReflectionTypeInferrerTest extends UnitTestSuite {

  test("inferScalaMetaTypeOf() for 'TestObjectWithDataMembersOnly' should return a corresponding Type.Singleton") {
    inferScalaMetaTypeOf(q"io.github.effiban.scala2java.core.reflection", q"TestObjectWithDataMembersOnly").value.structure shouldBe
      Type.Singleton(q"io.github.effiban.scala2java.core.reflection.TestObjectWithDataMembersOnly").structure
  }

  test("inferScalaMetaTypeOf() for 'scala.collection.immutable.Nil' should return 'scala.collection.immutable.List'") {
    inferScalaMetaTypeOf(q"scala.collection.immutable", q"Nil").value.structure shouldBe t"scala.collection.immutable.List[scala.Nothing]".structure
  }

  test("inferScalaMetaTypeOf() for 'scala.None' should return 'scala.Option'") {
    inferScalaMetaTypeOf(q"scala", q"None").value.structure shouldBe t"scala.Option[scala.Nothing]".structure
  }

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

  test("inferScalaMetaTypeOf() for 'TestParameterizedClassWithDataMembersOnly[scala.Int, scala.Long, java.lang.String].x' " +
    "should return 'scala.Int'") {
    val inferredType = inferScalaMetaTypeOf(
      t"io.github.effiban.scala2java.core.reflection.TestParameterizedClassWithDataMembersOnly",
      List(t"scala.Int", t"scala.Long", t"java.lang.String"),
      q"x"
    )
    inferredType.value.structure shouldBe t"scala.Int".structure
  }

  test("inferScalaMetaTypeOf() for 'TestParameterizedClassWithDataMembersOnly[AA, BB, CC].x' " +
    "should return 'AA'") {
    val inferredType = inferScalaMetaTypeOf(
      t"io.github.effiban.scala2java.core.reflection.TestParameterizedClassWithDataMembersOnly",
      List(t"AA", t"BB", t"CC"),
      q"x"
    )
    inferredType.value.structure shouldBe t"AA".structure
  }

  test("inferScalaMetaTypeOf() for 'TestParameterizedClassWithDataMembersOnly[scala.Int, scala.Long, java.lang.String].y' " +
    "should return '(scala.Int, scala.Long)'") {
    val inferredType = inferScalaMetaTypeOf(
      t"io.github.effiban.scala2java.core.reflection.TestParameterizedClassWithDataMembersOnly",
      List(t"scala.Int", t"scala.Long", t"java.lang.String"),
      q"y"
    )
    inferredType.value.structure shouldBe t"(scala.Int, scala.Long)".structure
  }

  test("inferScalaMetaTypeOf() for 'TestParameterizedClassWithDataMembersOnly[scala.Int, scala.Long, java.lang.String].z' " +
    "should return '(scala.Int, scala.Long, java.lang.String) => java.lang.String'") {
    val inferredType = inferScalaMetaTypeOf(
      t"io.github.effiban.scala2java.core.reflection.TestParameterizedClassWithDataMembersOnly",
      List(t"scala.Int", t"scala.Long", t"java.lang.String"),
      q"z"
    )
    inferredType.value.structure shouldBe t"(scala.Int, scala.Long, java.lang.String) => java.lang.String".structure
  }

  test("inferScalaMetaTypeOf() for 'TestParameterizedClassWithDataMembersOnly[scala.Int, scala.Long, java.lang.String].w' " +
    "should return 'scala.collection.immutable.List[scala.Int]'") {
    val inferredType = inferScalaMetaTypeOf(
      t"io.github.effiban.scala2java.core.reflection.TestParameterizedClassWithDataMembersOnly",
      List(t"scala.Int", t"scala.Long", t"java.lang.String"),
      q"w"
    )
    inferredType.value.structure shouldBe t"scala.collection.immutable.List[scala.Int]".structure
  }

  test("inferScalaMetaTypeOf() for 'TestParameterizedClassWithDataMembersOnly[scala.Int, scala.Long, java.lang.String].v' " +
    "should return 'scala.collection.immutable.List[scala.collection.immutable.List[scala.Int]]'") {
    val inferredType = inferScalaMetaTypeOf(
      t"io.github.effiban.scala2java.core.reflection.TestParameterizedClassWithDataMembersOnly",
      List(t"scala.Int", t"scala.Long", t"java.lang.String"),
      q"v"
    )
    inferredType.value.structure shouldBe t"scala.collection.immutable.List[scala.collection.immutable.List[scala.Int]]".structure
  }

  test("inferScalaMetaTypeOf() for 'TestChildParameterizedClassWithDataMembersOnly[scala.Int, scala.Long, java.lang.String].x' " +
    "should return 'scala.Int'") {
    val inferredType = inferScalaMetaTypeOf(
      t"io.github.effiban.scala2java.core.reflection.TestChildParameterizedClassWithDataMembersOnly",
      List(t"scala.Int", t"scala.Long", t"java.lang.String"),
      q"x"
    )
    inferredType.value.structure shouldBe t"scala.Int".structure
  }

  test("inferScalaMetaTypeOf() for 'TestChildParameterizedClassWithDataMembersOnly[scala.Int, scala.Long, java.lang.String].y' " +
    "should return '(scala.Int, scala.Long)'") {
    val inferredType = inferScalaMetaTypeOf(
      t"io.github.effiban.scala2java.core.reflection.TestChildParameterizedClassWithDataMembersOnly",
      List(t"scala.Int", t"scala.Long", t"java.lang.String"),
      q"y"
    )
    inferredType.value.structure shouldBe t"(scala.Int, scala.Long)".structure
  }

  class TestInnerClassWithDataMembersOnly {
    val x: Int = 3
  }

}
