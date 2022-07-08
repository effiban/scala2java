package effiban.scala2java

import effiban.scala2java.TraversalConstants.JavaPlaceholder
import effiban.scala2java.matchers.TreeMatcher.eqTree
import effiban.scala2java.stubbers.OutputWriterStubber.doWrite

import scala.meta.Term
import scala.meta.Term.{AnonymousFunction, Param}

class AnonymousFunctionTraverserImplTest extends UnitTestSuite {

  private val termFunctionTraverser = mock[TermFunctionTraverser]

  private val anonymousFunctionTraverser = new AnonymousFunctionTraverserImpl(termFunctionTraverser)

  test("traverse") {
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

    doWrite(expectedOutput).when(termFunctionTraverser).traverse(eqTree(function))

    anonymousFunctionTraverser.traverse(AnonymousFunction(functionBody))

    outputWriter.toString shouldBe expectedOutput
  }
}
