package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite

import scala.meta.{Pat, Term}

class PatExtractTraverserTest extends UnitTestSuite {

  test("traverse") {
    val patExtract = Pat.Extract(
      fun = Term.Name("MyClass"),
      args = List(Pat.Var(Term.Name("x")), Pat.Var(Term.Name("y")))
    )
    PatExtractTraverser.traverse(patExtract).structure shouldBe patExtract.structure
  }
}
