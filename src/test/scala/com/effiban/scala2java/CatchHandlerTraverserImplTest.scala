package com.effiban.scala2java

import com.effiban.scala2java.matchers.TreeListMatcher.eqTreeList
import com.effiban.scala2java.matchers.TreeMatcher.eqTree
import com.effiban.scala2java.stubbers.OutputWriterStubber.doWrite
import org.mockito.ArgumentMatchers

import scala.meta.Term.Block
import scala.meta.{Name, Term, Type}

class CatchHandlerTraverserImplTest extends UnitTestSuite {

  private val Param = termParam(Term.Name("e"), Type.Name("RuntimeException"))
  private val Statement = Term.Apply(Term.Select(Term.Name("log"), Term.Name("error")), List(Term.Name("e")))

  private val termParamListTraverser = mock[TermParamListTraverser]
  private val blockTraverser = mock[BlockTraverser]

  private val catchHandlerTraverser = new CatchHandlerTraverserImpl(termParamListTraverser, blockTraverser)

  test("traverse() when body is a statement") {
    doWrite("(RuntimeException e)")
      .when(termParamListTraverser).traverse(eqTreeList(List(Param)), onSameLine = ArgumentMatchers.eq(true))

    doWrite(
      """ {
        |  log.error(e);
        |}
        |""".stripMargin)
      .when(blockTraverser).traverse(
      block = eqTree(Block(List(Statement))),
      shouldReturnValue = ArgumentMatchers.eq(false),
      maybeInit = ArgumentMatchers.eq(None)
    )

    catchHandlerTraverser.traverse(Param, Statement)

    outputWriter.toString shouldBe
      """catch (RuntimeException e) {
        |  log.error(e);
        |}
        |""".stripMargin
  }

  test("traverse() when body is a block") {
    doWrite("(RuntimeException e)")
      .when(termParamListTraverser).traverse(eqTreeList(List(Param)), onSameLine = ArgumentMatchers.eq(true))

    doWrite(
      """ {
        |  log.error(e);
        |}
        |""".stripMargin)
      .when(blockTraverser).traverse(
      block = eqTree(Block(List(Statement))),
      shouldReturnValue = ArgumentMatchers.eq(false),
      maybeInit = ArgumentMatchers.eq(None)
    )

    catchHandlerTraverser.traverse(Param, Block(List(Statement)))

    outputWriter.toString shouldBe
      """catch (RuntimeException e) {
        |  log.error(e);
        |}
        |""".stripMargin
  }

  private def termParam(name: Name, decltpe: Type): Term.Param = {
    Term.Param(mods = Nil, name = name, decltpe = Some(decltpe), default = None)
  }
}
