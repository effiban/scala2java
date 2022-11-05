package io.github.effiban.scala2java.core.classifiers

import io.github.effiban.scala2java.core.entities.JavaScope
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite

import scala.meta.{Defn, Type}

class DefnTypeClassifierTest extends UnitTestSuite {


  test("isEnumTypeDef() when body is 'Value' and scope is 'Enum' should return true") {
    val defnType = Defn.Type(
      mods = Nil,
      name = Type.Name("X"),
      tparams = Nil,
      body = Type.Name("Value")
    )

    DefnTypeClassifier.isEnumTypeDef(defnType, JavaScope.Enum) shouldBe true
  }

  test("isEnumTypeDef() when body is 'Value' and scope is 'Class' should return false") {
    val defnType = Defn.Type(
      mods = Nil,
      name = Type.Name("X"),
      tparams = Nil,
      body = Type.Name("Value")
    )

    DefnTypeClassifier.isEnumTypeDef(defnType, JavaScope.Class) shouldBe false
  }

  test("isEnumTypeDef() when body is 'Blabla' and scope is 'Enum' should return false") {
    val defnType = Defn.Type(
      mods = Nil,
      name = Type.Name("X"),
      tparams = Nil,
      body = Type.Name("Blabla")
    )

    DefnTypeClassifier.isEnumTypeDef(defnType, JavaScope.Class) shouldBe false
  }

  test("isEnumTypeDef() when body is 'Blabla' and scope is 'Class' should return false") {
    val defnType = Defn.Type(
      mods = Nil,
      name = Type.Name("X"),
      tparams = Nil,
      body = Type.Name("Blabla")
    )

    DefnTypeClassifier.isEnumTypeDef(defnType, JavaScope.Class) shouldBe false
  }
}
