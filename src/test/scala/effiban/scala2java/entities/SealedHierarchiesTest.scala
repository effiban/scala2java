package effiban.scala2java.entities

import effiban.scala2java.testsuites.UnitTestSuite

import scala.meta.{Term, Type}

class SealedHierarchiesTest extends UnitTestSuite {

  test("getSubTypeNames() when exists as a parent type should return the sub-types") {
    val sealedHierarchies = SealedHierarchies(
      Map(
        Type.Name("A") -> List(Type.Name("B"), Term.Name("C")),
        Type.Name("D") -> List(Term.Name("E"))
      )
    )
    sealedHierarchies.getSubTypeNames(Type.Name("A")).structure shouldBe List(Type.Name("B"), Term.Name("C")).structure
  }

  test("getSubTypeNames() when exists as a child type only should return Nil") {
    val sealedHierarchies = SealedHierarchies(
      Map(
        Type.Name("A") -> List(Type.Name("B"), Type.Name("C")),
        Type.Name("D") -> List(Term.Name("E"))
      )
    )
    sealedHierarchies.getSubTypeNames(Type.Name("C")) shouldBe Nil
  }

  test("getSubTypeNames() when doesn't exist at all should return Nil") {
    val sealedHierarchies = SealedHierarchies(Map(Type.Name("A") -> List(Type.Name("B"), Type.Name("C"))))

    sealedHierarchies.getSubTypeNames(Type.Name("D")) shouldBe Nil
  }

  test("isSubType() when exists as a sub-type should return true") {
    val sealedHierarchies = SealedHierarchies(
      Map(
        Type.Name("A") -> List(Type.Name("B"), Term.Name("C")),
        Type.Name("D") -> List(Type.Name("E"))
      )
    )
    sealedHierarchies.isSubType(Term.Name("C")) shouldBe true
  }

  test("isSubType() when exists as a parent type only should return false") {
    val sealedHierarchies = SealedHierarchies(
      Map(
        Type.Name("A") -> List(Type.Name("B"), Term.Name("C")),
        Type.Name("D") -> List(Type.Name("E"))
      )
    )
    sealedHierarchies.isSubType(Term.Name("A")) shouldBe false
  }

  test("asStringMap()") {
    val sealedHierarchies = SealedHierarchies(
      Map(
        Type.Name("A") -> List(Type.Name("B"), Term.Name("C")),
        Type.Name("D") -> List(Type.Name("E"))
      )
    )
    sealedHierarchies.asStringMap() shouldBe Map(
      "A" -> List("B", "C"),
      "D" -> List("E")
    )
  }
}
