package io.github.effiban.scala2java.traversers

import io.github.effiban.scala2java.contexts.{BlockContext, StatContext}
import io.github.effiban.scala2java.matchers.BlockContextMatcher.eqBlockContext
import io.github.effiban.scala2java.matchers.CombinedMatchers.eqTreeList
import io.github.effiban.scala2java.matchers.TreeMatcher.eqTree
import io.github.effiban.scala2java.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.testsuites.UnitTestSuite
import org.mockito.ArgumentMatchers

import scala.meta.{Name, Term, Type}

class CatchHandlerTraverserImplTest extends UnitTestSuite {

  private val Param = termParam(Term.Name("e"), Type.Name("RuntimeException"))
  private val LogStatement = Term.Apply(Term.Select(Term.Name("log"), Term.Name("error")), List(Term.Name("e")))
  private val GetMsgStatement = Term.Apply(Term.Select(Term.Name("e"), Term.Name("getMessage")), Nil)

  private val termParamListTraverser = mock[TermParamListTraverser]
  private val blockTraverser = mock[BlockTraverser]

  private val catchHandlerTraverser = new CatchHandlerTraverserImpl(termParamListTraverser, blockTraverser)

  test("traverse() when shouldReturnValue=No") {
    doWrite("(RuntimeException e)")
      .when(termParamListTraverser).traverse(
      eqTreeList(List(Param)),
      context = ArgumentMatchers.eq(StatContext()),
      onSameLine = ArgumentMatchers.eq(true)
    )

    doWrite(
      """ {
        |  log.error(e);
        |}
        |""".stripMargin)
      .when(blockTraverser).traverse(
      stat = eqTree(LogStatement),
      context = eqBlockContext(BlockContext())
    )

    catchHandlerTraverser.traverse(Param, LogStatement)

    outputWriter.toString shouldBe
      """catch (RuntimeException e) {
        |  log.error(e);
        |}
        |""".stripMargin
  }

  test("traverse() when shouldReturnValue=Yes") {
    doWrite("(RuntimeException e)")
      .when(termParamListTraverser).traverse(
      eqTreeList(List(Param)),
      context = ArgumentMatchers.eq(StatContext()),
      onSameLine = ArgumentMatchers.eq(true)
    )

    doWrite(
      """ {
        |  return e.getMessage();
        |}
        |""".stripMargin)
      .when(blockTraverser).traverse(
      stat = eqTree(GetMsgStatement),
      context = eqBlockContext(BlockContext())
    )

    catchHandlerTraverser.traverse(Param, GetMsgStatement)

    outputWriter.toString shouldBe
      """catch (RuntimeException e) {
        |  return e.getMessage();
        |}
        |""".stripMargin
  }

  private def termParam(name: Name, decltpe: Type): Term.Param = {
    Term.Param(mods = Nil, name = name, decltpe = Some(decltpe), default = None)
  }
}
