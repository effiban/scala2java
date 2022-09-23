package effiban.scala2java.traversers

import effiban.scala2java.testsuites.UnitTestSuite

import scala.meta.{Term, Type}

class PermittedSubTypeNameListTraverserImplTest extends UnitTestSuite {

  private val permittedSubTypeNameListTraverser = new PermittedSubTypeNameListTraverserImpl(new ArgumentListTraverserImpl())

  test("traverse() when one") {
    val permittedSubTypeName = Type.Name("A")

    permittedSubTypeNameListTraverser.traverse(List(permittedSubTypeName))

    outputWriter.toString shouldBe "permits A"
  }

  test("traverse() when two") {
    val permittedSubTypeNames = List(Type.Name("A"), Term.Name("B"))

    permittedSubTypeNameListTraverser.traverse(permittedSubTypeNames)

    outputWriter.toString shouldBe "permits A, B"
  }
}
