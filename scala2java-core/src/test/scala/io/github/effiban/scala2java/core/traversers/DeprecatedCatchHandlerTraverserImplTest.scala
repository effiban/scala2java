package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.{BlockContext, CatchHandlerContext}
import io.github.effiban.scala2java.core.entities.Decision.Yes
import io.github.effiban.scala2java.core.matchers.BlockContextMatcher.eqBlockContext
import io.github.effiban.scala2java.core.renderers.CatchArgumentRenderer
import io.github.effiban.scala2java.core.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.{Case, Term, XtensionQuasiquoteCaseOrPattern}

class DeprecatedCatchHandlerTraverserImplTest extends UnitTestSuite {

  private val CatchArg = p"e"
  private val TraversedCatchArg = p"e: RuntimeException"
  private val RenderedCatchArg = "(RuntimeException e)"
  private val LogStatement = Term.Apply(Term.Select(Term.Name("log"), Term.Name("error")), List(Term.Name("e")))
  private val GetMsgStatement = Term.Apply(Term.Select(Term.Name("e"), Term.Name("getMessage")), Nil)

  private val catchArgumentTraverser = mock[CatchArgumentTraverser]
  private val catchArgumentRenderer = mock[CatchArgumentRenderer]
  private val blockTraverser = mock[DeprecatedBlockTraverser]

  private val catchHandlerTraverser = new DeprecatedCatchHandlerTraverserImpl(
    catchArgumentTraverser,
    catchArgumentRenderer,
    blockTraverser
  )

  test("traverse() when shouldReturnValue=No") {
    val CatchCase = Case(pat = CatchArg, cond = None, body = LogStatement)

    doReturn(TraversedCatchArg).when(catchArgumentTraverser).traverse(eqTree(CatchArg))
    doWrite(RenderedCatchArg).when(catchArgumentRenderer).render(eqTree(TraversedCatchArg))

    doWrite(
      """ {
        |  log.error(e);
        |}
        |""".stripMargin)
      .when(blockTraverser).traverse(
      stat = eqTree(LogStatement),
      context = eqBlockContext(BlockContext())
    )

    catchHandlerTraverser.traverse(CatchCase)

    outputWriter.toString shouldBe
      """catch (RuntimeException e) {
        |  log.error(e);
        |}
        |""".stripMargin
  }

  test("traverse() when shouldReturnValue=Yes") {
    val CatchCase = Case(pat = CatchArg, cond = None, body = GetMsgStatement)

    doReturn(TraversedCatchArg).when(catchArgumentTraverser).traverse(eqTree(CatchArg))
    doWrite(RenderedCatchArg).when(catchArgumentRenderer).render(eqTree(TraversedCatchArg))

    doWrite(
      """ {
        |  return e.getMessage();
        |}
        |""".stripMargin)
      .when(blockTraverser).traverse(
      stat = eqTree(GetMsgStatement),
      context = eqBlockContext(BlockContext(shouldReturnValue = Yes))
    )

    catchHandlerTraverser.traverse(CatchCase, context = CatchHandlerContext(shouldReturnValue = Yes))

    outputWriter.toString shouldBe
      """catch (RuntimeException e) {
        |  return e.getMessage();
        |}
        |""".stripMargin
  }
}
