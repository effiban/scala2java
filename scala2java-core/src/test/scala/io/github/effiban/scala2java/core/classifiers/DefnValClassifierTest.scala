package io.github.effiban.scala2java.core.classifiers

import io.github.effiban.scala2java.core.entities.JavaScope
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite

import scala.meta.{Defn, Pat, Term}

class DefnValClassifierTest extends UnitTestSuite {


  test("isEnumConstantList() when body is 'Value' and scope is 'Enum' should return true") {
    val defnVal = Defn.Val(
      mods = Nil,
      pats = List(Pat.Var(Term.Name("First")), Pat.Var(Term.Name("Second"))),
      decltpe = None,
      rhs = Term.Name("Value")
    )

    DefnValClassifier.isEnumConstantList(defnVal, JavaScope.Enum) shouldBe true
  }

  test("isEnumConstantList() when RHS is 'Value' and scope is 'Class' should return false") {
    val defnVal = Defn.Val(
      mods = Nil,
      pats = List(Pat.Var(Term.Name("First")), Pat.Var(Term.Name("Second"))),
      decltpe = None,
      rhs = Term.Name("Value")
    )

    DefnValClassifier.isEnumConstantList(defnVal, JavaScope.Class) shouldBe false
  }

  test("isEnumConstantList() when RHS is 'Blabla' and scope is 'Enum' should return false") {
    val defnVal = Defn.Val(
      mods = Nil,
      pats = List(Pat.Var(Term.Name("First")), Pat.Var(Term.Name("Second"))),
      decltpe = None,
      rhs = Term.Name("Blabla")
    )

    DefnValClassifier.isEnumConstantList(defnVal, JavaScope.Class) shouldBe false
  }

  test("isEnumConstantList() when RHS is 'Blabla' and scope is 'Class' should return false") {
    val defnVal = Defn.Val(
      mods = Nil,
      pats = List(Pat.Var(Term.Name("First")), Pat.Var(Term.Name("Second"))),
      decltpe = None,
      rhs = Term.Name("Blabla")
    )

    DefnValClassifier.isEnumConstantList(defnVal, JavaScope.Class) shouldBe false
  }
}
