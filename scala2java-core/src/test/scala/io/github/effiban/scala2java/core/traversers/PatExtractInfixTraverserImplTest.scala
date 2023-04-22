package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite

import scala.meta.{Pat, Term}

class PatExtractInfixTraverserImplTest extends UnitTestSuite {

  test("traverse") {
    val caseClassCreator = Term.Name("MyClass")
    val lhs = Pat.Var(Term.Name("x"))
    val rhs = List(Pat.Var(Term.Name("y")), Pat.Var(Term.Name("z")))

    val patExtractInfix = Pat.ExtractInfix(lhs = lhs, op = caseClassCreator, rhs = rhs)

    val expectedPatExtract = Pat.Extract(fun = caseClassCreator, args = lhs :: rhs)

    PatExtractInfixTraverser.traverse(patExtractInfix).structure shouldBe expectedPatExtract.structure
  }
}
