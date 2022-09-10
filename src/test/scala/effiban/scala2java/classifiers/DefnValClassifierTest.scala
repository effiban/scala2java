package effiban.scala2java.classifiers

import effiban.scala2java.entities.JavaTreeType
import effiban.scala2java.testsuites.UnitTestSuite

import scala.meta.{Defn, Pat, Term}

class DefnValClassifierTest extends UnitTestSuite {


  test("isEnumConstantList() when body is 'Value' and scope is 'Enum' should return true") {
    val defnVal = Defn.Val(
      mods = Nil,
      pats = List(Pat.Var(Term.Name("First")), Pat.Var(Term.Name("Second"))),
      decltpe = None,
      rhs = Term.Name("Value")
    )

    DefnValClassifier.isEnumConstantList(defnVal, JavaTreeType.Enum) shouldBe true
  }

  test("isEnumConstantList() when RHS is 'Value' and scope is 'Class' should return false") {
    val defnVal = Defn.Val(
      mods = Nil,
      pats = List(Pat.Var(Term.Name("First")), Pat.Var(Term.Name("Second"))),
      decltpe = None,
      rhs = Term.Name("Value")
    )

    DefnValClassifier.isEnumConstantList(defnVal, JavaTreeType.Class) shouldBe false
  }

  test("isEnumConstantList() when RHS is 'Blabla' and scope is 'Enum' should return false") {
    val defnVal = Defn.Val(
      mods = Nil,
      pats = List(Pat.Var(Term.Name("First")), Pat.Var(Term.Name("Second"))),
      decltpe = None,
      rhs = Term.Name("Blabla")
    )

    DefnValClassifier.isEnumConstantList(defnVal, JavaTreeType.Class) shouldBe false
  }

  test("isEnumConstantList() when RHS is 'Blabla' and scope is 'Class' should return false") {
    val defnVal = Defn.Val(
      mods = Nil,
      pats = List(Pat.Var(Term.Name("First")), Pat.Var(Term.Name("Second"))),
      decltpe = None,
      rhs = Term.Name("Blabla")
    )

    DefnValClassifier.isEnumConstantList(defnVal, JavaTreeType.Class) shouldBe false
  }
}
