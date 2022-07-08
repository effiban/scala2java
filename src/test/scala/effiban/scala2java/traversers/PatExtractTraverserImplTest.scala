package effiban.scala2java.traversers

import effiban.scala2java.testsuites.UnitTestSuite

import scala.meta.{Pat, Term}

class PatExtractTraverserImplTest extends UnitTestSuite {

  val patExtractTraverser = new PatExtractTraverserImpl()

  test("traverse") {
    val patExtract = Pat.Extract(
      fun = Term.Name("MyClass"),
      args = List(Pat.Var(Term.Name("x")), Pat.Var(Term.Name("y")))
    )
    patExtractTraverser.traverse(patExtract)

    outputWriter.toString shouldBe "/* MyClass(x, y) */"
  }

}