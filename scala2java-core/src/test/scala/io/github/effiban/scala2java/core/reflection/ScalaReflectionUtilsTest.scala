package io.github.effiban.scala2java.core.reflection

import io.github.effiban.scala2java.core.entities.ReflectedEntities.RuntimeMirror
import io.github.effiban.scala2java.core.reflection.ScalaReflectionUtils.{isTermMemberOf, isTypeMemberOf}
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite

import scala.meta.{Term, Type}

class ScalaReflectionUtilsTest extends UnitTestSuite {

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
