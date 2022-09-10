package effiban.scala2java.classifiers

import effiban.scala2java.entities.JavaTreeType
import effiban.scala2java.testsuites.UnitTestSuite

import scala.meta.{Defn, Type}

class DefnTypeClassifierTest extends UnitTestSuite {


  test("isEnumTypeDef() when body is 'Value' and scope is 'Enum' should return true") {
    val defnType = Defn.Type(
      mods = Nil,
      name = Type.Name("X"),
      tparams = Nil,
      body = Type.Name("Value")
    )

    DefnTypeClassifier.isEnumTypeDef(defnType, JavaTreeType.Enum) shouldBe true
  }

  test("isEnumTypeDef() when body is 'Value' and scope is 'Class' should return false") {
    val defnType = Defn.Type(
      mods = Nil,
      name = Type.Name("X"),
      tparams = Nil,
      body = Type.Name("Value")
    )

    DefnTypeClassifier.isEnumTypeDef(defnType, JavaTreeType.Class) shouldBe false
  }

  test("isEnumTypeDef() when body is 'Blabla' and scope is 'Enum' should return false") {
    val defnType = Defn.Type(
      mods = Nil,
      name = Type.Name("X"),
      tparams = Nil,
      body = Type.Name("Blabla")
    )

    DefnTypeClassifier.isEnumTypeDef(defnType, JavaTreeType.Class) shouldBe false
  }

  test("isEnumTypeDef() when body is 'Blabla' and scope is 'Class' should return false") {
    val defnType = Defn.Type(
      mods = Nil,
      name = Type.Name("X"),
      tparams = Nil,
      body = Type.Name("Blabla")
    )

    DefnTypeClassifier.isEnumTypeDef(defnType, JavaTreeType.Class) shouldBe false
  }
}
