package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite

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
