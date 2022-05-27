package com.effiban.scala2java

import com.effiban.scala2java.stubs.StubPatExtractTraverser

import scala.meta.{Pat, Term}

class PatExtractInfixTraverserImplTest extends UnitTestSuite {

  val patExtractInfixTraverser = new PatExtractInfixTraverserImpl(new StubPatExtractTraverser())

  test("traverse") {
    val patExtractInfix = Pat.ExtractInfix(
      lhs = Pat.Var(Term.Name("x")),
      op = Term.Name("MyClass"),
      rhs = List(Pat.Var(Term.Name("y")), Pat.Var(Term.Name("z")))
    )
    patExtractInfixTraverser.traverse(patExtractInfix)

    outputWriter.toString shouldBe "/* MyClass(x, y, z) */"
  }

}
