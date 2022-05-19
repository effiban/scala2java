package com.effiban.scala2java

import com.effiban.scala2java.stubs.StubTermFunctionTraverser

import scala.meta.Term
import scala.meta.Term.AnonymousFunction

class AnonymousFunctionTraverserImplTest extends UnitTestSuite {

  private val anonymousFunctionTraverser = new AnonymousFunctionTraverserImpl(new StubTermFunctionTraverser)

  test("traverse") {
    anonymousFunctionTraverser.traverse(AnonymousFunction(
      Term.Block(
        List(Term.Name("dummy_statement_1"), Term.Name("dummy_statement_2"))
      )))

    outputWriter.toString shouldBe """__ => {
                            |  dummy_statement_1
                            |  dummy_statement_2
                            |}""".stripMargin
  }
}
