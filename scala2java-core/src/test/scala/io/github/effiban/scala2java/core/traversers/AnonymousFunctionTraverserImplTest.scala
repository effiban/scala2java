package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.entities.Decision.{Uncertain, Yes}
import io.github.effiban.scala2java.core.entities.TraversalConstants.JavaPlaceholder
import io.github.effiban.scala2java.core.matchers.TreeMatcher.eqTree
import io.github.effiban.scala2java.core.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import org.mockito.ArgumentMatchers

import scala.meta.Term
import scala.meta.Term.{AnonymousFunction, Param}

class AnonymousFunctionTraverserImplTest extends UnitTestSuite {

  private val termFunctionTraverser = mock[TermFunctionTraverser]

  private val anonymousFunctionTraverser = new AnonymousFunctionTraverserImpl(termFunctionTraverser)

  test("traverse()") {
    val functionBody = Term.Block(
      List(Term.Name("dummy_statement_1"), Term.Name("dummy_statement_2"))
    )
    val function = Term.Function(
      params = List(Param(name = Term.Name(JavaPlaceholder), mods = Nil, decltpe = None, default = None)),
      body = functionBody)

    val expectedOutput =
      """__ => {
        |  dummy_statement_1;
        |  dummy_statement_2;
        |}""".stripMargin

    doWrite(expectedOutput).when(termFunctionTraverser).traverse(eqTree(function), ArgumentMatchers.eq(Uncertain))

    anonymousFunctionTraverser.traverse(AnonymousFunction(functionBody))

    outputWriter.toString shouldBe expectedOutput
  }

  test("traverse() when shouldBodyReturnValue=Yes") {
    val functionBody = Term.Block(
      List(Term.Name("dummy_statement_1"), Term.Name("dummy_statement_2"))
    )
    val function = Term.Function(
      params = List(Param(name = Term.Name(JavaPlaceholder), mods = Nil, decltpe = None, default = None)),
      body = functionBody)

    val expectedOutput =
      """__ => {
        |  dummy_statement_1;
        |  return dummy_statement_2;
        |}""".stripMargin

    doWrite(expectedOutput).when(termFunctionTraverser).traverse(eqTree(function), ArgumentMatchers.eq(Yes))

    anonymousFunctionTraverser.traverse(AnonymousFunction(functionBody), shouldBodyReturnValue = Yes)

    outputWriter.toString shouldBe expectedOutput
  }
}
