package io.github.effiban.scala2java.core.classifiers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.spi.entities.JavaScope

import scala.meta.{Defn, Mod, Pat, Term}

class DefnVarClassifierTest extends UnitTestSuite {


  test("isEnumConstantList() when is 'final', body is 'Value' and scope is 'Enum' should return true") {
    val defnVar = Defn.Var(
      mods = List(Mod.Final()),
      pats = List(Pat.Var(Term.Name("First")), Pat.Var(Term.Name("Second"))),
      decltpe = None,
      rhs = Some(Term.Name("Value"))
    )

    DefnVarClassifier.isEnumConstantList(defnVar, JavaScope.Enum) shouldBe true
  }

  test("isEnumConstantList() when not 'final', body is 'Value' and scope is 'Enum' should return false") {
    val defnVar = Defn.Var(
      mods = Nil,
      pats = List(Pat.Var(Term.Name("First")), Pat.Var(Term.Name("Second"))),
      decltpe = None,
      rhs = Some(Term.Name("Value"))
    )

    DefnVarClassifier.isEnumConstantList(defnVar, JavaScope.Enum) shouldBe false
  }

  test("isEnumConstantList() when RHS is 'Value' and scope is 'Class' should return false") {
    val defnVar = Defn.Var(
      mods = List(Mod.Final()),
      pats = List(Pat.Var(Term.Name("First")), Pat.Var(Term.Name("Second"))),
      decltpe = None,
      rhs = Some(Term.Name("Value"))
    )

    DefnVarClassifier.isEnumConstantList(defnVar, JavaScope.Class) shouldBe false
  }

  test("isEnumConstantList() when RHS is 'Blabla' and scope is 'Enum' should return false") {
    val defnVar = Defn.Var(
      mods = List(Mod.Final()),
      pats = List(Pat.Var(Term.Name("First")), Pat.Var(Term.Name("Second"))),
      decltpe = None,
      rhs = Some(Term.Name("Blabla"))
    )

    DefnVarClassifier.isEnumConstantList(defnVar, JavaScope.Class) shouldBe false
  }

  test("isEnumConstantList() when RHS is 'Blabla' and scope is 'Class' should return false") {
    val defnVar = Defn.Var(
      mods = Nil,
      pats = List(Pat.Var(Term.Name("First")), Pat.Var(Term.Name("Second"))),
      decltpe = None,
      rhs = Some(Term.Name("Blabla"))
    )

    DefnVarClassifier.isEnumConstantList(defnVar, JavaScope.Class) shouldBe false
  }
}
