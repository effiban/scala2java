package com.effiban.scala2java

import org.mockito.ArgumentMatchers.any

import scala.meta.Term
import scala.meta.Term.AnonymousFunction

class AnonymousFunctionTraverserImplTest extends UnitTestSuite {

  private val termFunctionTraverser = mock[TermFunctionTraverser]

  private val anonymousFunctionTraverser = new AnonymousFunctionTraverserImpl(termFunctionTraverser)

  override def beforeEach(): Unit = {
    super.beforeEach()
    doAnswer((function: Term.Function) => outputWriter.write(function.toString())).when(termFunctionTraverser).traverse(any[Term.Function])
  }

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
