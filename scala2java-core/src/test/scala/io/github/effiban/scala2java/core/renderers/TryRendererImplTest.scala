package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.renderers.contexts.{BlockRenderContext, TryRenderContext}
import io.github.effiban.scala2java.core.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchersSugar.eqTo

import scala.meta.Term.{Apply, Block}
import scala.meta.{Case, Pat, Term, Type}

class TryRendererImplTest extends UnitTestSuite {

  private val TryBlock = Term.Block(List(Term.Apply(Term.Name("doSomething"), Nil)))

  private val TermName1: Term.Name = Term.Name("e1")
  private val TermName2: Term.Name = Term.Name("e2")

  private val TypeName1: Type.Name = Type.Name("IllegalArgumentException")
  private val TypeName2: Type.Name = Type.Name("IllegalStateException")

  private val CatchPat1 = Pat.Typed(TermName1, TypeName1)
  private val CatchPat2 = Pat.Typed(TermName2, TypeName2)

  private val CatchBodyMethod: Term.Select = Term.Select(Term.Name("log"), Term.Name("error"))

  private val CatchStatement1: Apply = Term.Apply(CatchBodyMethod, List(TermName1))
  private val CatchStatement2: Apply = Term.Apply(CatchBodyMethod, List(TermName2))

  private val CatchCase1 = Case(
    pat = CatchPat1,
    cond = None,
    body = Block(List(CatchStatement1))
  )
  private val CatchCase2 = Case(
    pat = CatchPat2,
    cond = None,
    body = Block(List(CatchStatement2))
  )

  private val FinallyBlock = Term.Block(List(Term.Apply(Term.Name("cleanup"), Nil)))

  private val blockRenderer = mock[BlockRenderer]
  private val catchHandlerRenderer = mock[CatchHandlerRenderer]
  private val finallyRenderer = mock[FinallyRenderer]

  private val tryRenderer = new TryRendererImpl(
    blockRenderer,
    catchHandlerRenderer,
    finallyRenderer
  )

  test("render with no 'catch' cases and no 'finally'") {
    val `try` = Term.Try(
      expr = TryBlock,
      catchp = Nil,
      finallyp = None
    )

    doWrite(
      """ {
        |  doSomething();
        |}
        |""".stripMargin)
      .when(blockRenderer).render(block = eqTree(TryBlock), context = eqTo(BlockRenderContext()))

    tryRenderer.render(`try`)

    outputWriter.toString shouldBe
      """try {
        |  doSomething();
        |}
        |""".stripMargin
  }

  test("render with no 'catch' cases and no 'finally', when has uncertain return") {
    val `try` = Term.Try(
      expr = TryBlock,
      catchp = Nil,
      finallyp = None
    )
    val exprContext = BlockRenderContext(uncertainReturn = true)
    val tryContext = TryRenderContext(uncertainReturn = true)

    doWrite(
      """ {
        |  /* return? */doSomething();
        |}
        |""".stripMargin)
      .when(blockRenderer).render(block = eqTree(TryBlock), context = eqTo(exprContext))

    tryRenderer.render(`try`, tryContext)

    outputWriter.toString shouldBe
      """try {
        |  /* return? */doSomething();
        |}
        |""".stripMargin
  }

  test("render with one 'catch' case and no 'finally'") {
    val `try` = Term.Try(
      expr = TryBlock,
      catchp = List(
        CatchCase1
      ),
      finallyp = None
    )

    doWrite(
      """ {
        |  doSomething();
        |}
        |""".stripMargin)
      .when(blockRenderer).render(block = eqTree(TryBlock),context = eqTo(BlockRenderContext()))

    doWrite(
      """catch (IllegalArgumentException e1) {
        |  log.error(e1);
        |}
        |""".stripMargin)
      .when(catchHandlerRenderer).render(
      eqTree(CatchCase1),
      eqTo(BlockRenderContext()))

    tryRenderer.render(`try`)

    outputWriter.toString shouldBe
      """try {
        |  doSomething();
        |}
        |catch (IllegalArgumentException e1) {
        |  log.error(e1);
        |}
        |""".stripMargin
  }

  test("render with one 'catch' case and no 'finally', when has uncertain return") {
    val `try` = Term.Try(
      expr = TryBlock,
      catchp = List(
        CatchCase1
      ),
      finallyp = None
    )

    val clauseContext = BlockRenderContext(uncertainReturn = true)
    val tryContext = TryRenderContext(uncertainReturn = true)

    doWrite(
      """ {
        |  /* return? */doSomething();
        |}
        |""".stripMargin)
      .when(blockRenderer).render(
      block = eqTree(TryBlock),
      context = eqTo(clauseContext)
    )

    doWrite(
      """catch (IllegalArgumentException e1) {
        |  /* return? */"failed";
        |}
        |""".stripMargin)
      .when(catchHandlerRenderer).render(
      catchCase = eqTree(CatchCase1),
      context = eqTo(clauseContext))

    tryRenderer.render(`try` = `try`, context = tryContext)

    outputWriter.toString shouldBe
      """try {
        |  /* return? */doSomething();
        |}
        |catch (IllegalArgumentException e1) {
        |  /* return? */"failed";
        |}
        |""".stripMargin
  }

  test("render with two 'catch' cases and no 'finally'") {
    val `try` = Term.Try(
      expr = TryBlock,
      catchp = List(
        CatchCase1,
        CatchCase2
      ),
      finallyp = None
    )

    doWrite(
      """ {
        |  doSomething();
        |}
        |""".stripMargin)
      .when(blockRenderer).render(block = eqTree(TryBlock), context = eqTo(BlockRenderContext()))

    doWrite(
      """catch (IllegalArgumentException e1) {
        |  log.error(e1);
        |}
        |""".stripMargin)
      .when(catchHandlerRenderer).render(
      eqTree(CatchCase1),
      eqTo(BlockRenderContext())
    )

    doWrite(
      """catch (IllegalStateException e2) {
        |  log.error(e2);
        |}
        |""".stripMargin)
      .when(catchHandlerRenderer).render(
      eqTree(CatchCase2),
      eqTo(BlockRenderContext())
    )

    tryRenderer.render(`try`)

    outputWriter.toString shouldBe
      """try {
        |  doSomething();
        |}
        |catch (IllegalArgumentException e1) {
        |  log.error(e1);
        |}
        |catch (IllegalStateException e2) {
        |  log.error(e2);
        |}
        |""".stripMargin
  }

  test("render with two 'catch' cases and no 'finally', when has uncertain return") {
    val `try` = Term.Try(
      expr = TryBlock,
      catchp = List(
        CatchCase1,
        CatchCase2
      ),
      finallyp = None
    )

    val clauseContext = BlockRenderContext(uncertainReturn = true)
    val tryContext = TryRenderContext(uncertainReturn = true)

    doWrite(
      """ {
        |  /* return? */doSomething();
        |}
        |""".stripMargin)
      .when(blockRenderer).render(block = eqTree(TryBlock), context = eqTo(clauseContext))

    doWrite(
      """catch (IllegalArgumentException e1) {
        |  /* return? */log.error(e1);
        |}
        |""".stripMargin)
      .when(catchHandlerRenderer).render(
      eqTree(CatchCase1),
      eqTo(clauseContext)
    )

    doWrite(
      """catch (IllegalStateException e2) {
        |  /* return? */log.error(e2);
        |}
        |""".stripMargin)
      .when(catchHandlerRenderer).render(
      eqTree(CatchCase2),
      eqTo(clauseContext)
    )

    tryRenderer.render(`try`, tryContext)

    outputWriter.toString shouldBe
      """try {
        |  /* return? */doSomething();
        |}
        |catch (IllegalArgumentException e1) {
        |  /* return? */log.error(e1);
        |}
        |catch (IllegalStateException e2) {
        |  /* return? */log.error(e2);
        |}
        |""".stripMargin
  }

  test("render with one 'catch' case and a 'finally'") {
    val `try` = Term.Try(
      expr = TryBlock,
      catchp = List(
        CatchCase1
      ),
      finallyp = Some(FinallyBlock)
    )

    doWrite(
      """ {
        |  doSomething();
        |}
        |""".stripMargin)
      .when(blockRenderer).render(block = eqTree(TryBlock), context = eqTo(BlockRenderContext()))

    doWrite(
      """catch (IllegalArgumentException e1) {
        |  log.error(e1);
        |}
        |""".stripMargin)
      .when(catchHandlerRenderer).render(
      eqTree(CatchCase1),
      eqTo(BlockRenderContext())
    )

    doWrite(
      """finally {
        |  cleanup();
        |}
        |""".stripMargin)
      .when(finallyRenderer).render(eqTree(FinallyBlock))

    tryRenderer.render(`try`)

    outputWriter.toString shouldBe
      """try {
        |  doSomething();
        |}
        |catch (IllegalArgumentException e1) {
        |  log.error(e1);
        |}
        |finally {
        |  cleanup();
        |}
        |""".stripMargin
  }
}
