package com.effiban.scala2java

import com.effiban.scala2java.stubs.StubTermFunctionTraverser

import scala.meta.{Case, Pat, Term}

class PartialFunctionTraverserImplTest extends UnitTestSuite {

  private val partialFunctionTraverser = new PartialFunctionTraverserImpl(new StubTermFunctionTraverser())

  test("traverse") {
    val partialFunction = Term.PartialFunction(
      List(
        Case(pat = Pat.Var(Term.Name("x")), cond = None, body = Term.Name("X")),
        Case(pat = Pat.Var(Term.Name("y")), cond = None, body = Term.Name("Y"))
      )
    )

    partialFunctionTraverser.traverse(partialFunction)

    outputWriter.toString shouldBe
      """arg => arg match {
        |  case x => X
        |  case y => Y
        |}""".stripMargin
  }
}
