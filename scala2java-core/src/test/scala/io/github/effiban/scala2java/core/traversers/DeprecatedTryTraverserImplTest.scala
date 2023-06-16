package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.{BlockContext, CatchHandlerContext, TryContext}
import io.github.effiban.scala2java.core.entities.Decision.Yes
import io.github.effiban.scala2java.core.matchers.BlockContextMatcher.eqBlockContext
import io.github.effiban.scala2java.core.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchersSugar.eqTo

import scala.meta.Term.{Apply, Block}
import scala.meta.{Case, Pat, Term, Type}

class DeprecatedTryTraverserImplTest extends UnitTestSuite {

  private val TryStatement = Term.Apply(Term.Name("doSomething"), Nil)

  private val TermName1: Term.Name = Term.Name("e1")
  private val TermName2: Term.Name = Term.Name("e2")

  private val TypeName1: Type.Name = Type.Name("IllegalArgumentException")
  private val TypeName2: Type.Name = Type.Name("IllegalStateException")

  private val CatchPat1 = Pat.Typed(TermName1, TypeName1)
  private val CatchPat2 = Pat.Typed(TermName2, TypeName2)
  private val UnsupportedCatchPat = Pat.Repeated(Term.Name("e"))

  private val CatchBodyMethod: Term.Select = Term.Select(Term.Name("log"), Term.Name("error"))

  private val CatchStatement1: Apply = Term.Apply(CatchBodyMethod, List(TermName1))
  private val CatchStatement2: Apply = Term.Apply(CatchBodyMethod, List(TermName2))

  private val CatchCase1 = Case(
    pat = CatchPat1,
    cond = None,
    body = CatchStatement1
  )
  private val CatchCase2 = Case(
    pat = CatchPat2,
    cond = None,
    body = CatchStatement2
  )
  private val UnsupportedCatchCase = Case(
    pat = UnsupportedCatchPat,
    cond = None,
    body = Term.Apply(CatchBodyMethod, List(Term.Name("e")))
  )

  private val FinallyStatement = Term.Apply(Term.Name("cleanup"), Nil)

  private val blockTraverser = mock[DeprecatedBlockTraverser]
  private val catchHandlerTraverser = mock[DeprecatedCatchHandlerTraverser]
  private val finallyTraverser = mock[DeprecatedFinallyTraverser]

  private val tryTraverser = new DeprecatedTryTraverserImpl(
    blockTraverser,
    catchHandlerTraverser,
    finallyTraverser
  )

  test("traverse with single statement, no 'catch' cases and no 'finally'") {
    val `try` = Term.Try(
      expr = TryStatement,
      catchp = Nil,
      finallyp = None
    )

    doWrite(
      """ {
        |  doSomething();
        |}
        |""".stripMargin)
      .when(blockTraverser).traverse(stat = eqTree(TryStatement), context = eqBlockContext(BlockContext()))

    tryTraverser.traverse(`try`)

    outputWriter.toString shouldBe
      """try {
        |  doSomething();
        |}
        |""".stripMargin
  }

  test("traverse with a block, no 'catch' cases and no 'finally'") {
    val `try` = Term.Try(
      expr = Block(List(TryStatement)),
      catchp = Nil,
      finallyp = None
    )

    doWrite(
      """ {
        |  doSomething();
        |}
        |""".stripMargin)
      .when(blockTraverser).traverse(stat = eqTree(Block(List(TryStatement))), context = eqBlockContext(BlockContext()))

    tryTraverser.traverse(`try`)

    outputWriter.toString shouldBe
      """try {
        |  doSomething();
        |}
        |""".stripMargin
  }

  test("traverse with single statement, one 'catch' case and no 'finally'") {
    val `try` = Term.Try(
      expr = TryStatement,
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
      .when(blockTraverser).traverse(stat = eqTree(TryStatement),context = eqBlockContext(BlockContext()))

    doWrite(
      """catch (IllegalArgumentException e1) {
        |  log.error(e1);
        |}
        |""".stripMargin)
      .when(catchHandlerTraverser).traverse(
      eqTree(CatchCase1),
      eqTo(CatchHandlerContext()))

    tryTraverser.traverse(`try`)

    outputWriter.toString shouldBe
      """try {
        |  doSomething();
        |}
        |catch (IllegalArgumentException e1) {
        |  log.error(e1);
        |}
        |""".stripMargin
  }

  test("traverse with single statement, two 'catch' cases and no 'finally'") {
    val `try` = Term.Try(
      expr = TryStatement,
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
      .when(blockTraverser).traverse(stat = eqTree(TryStatement), context = eqBlockContext(BlockContext()))

    doWrite(
      """catch (IllegalArgumentException e1) {
        |  log.error(e1);
        |}
        |""".stripMargin)
      .when(catchHandlerTraverser).traverse(
      eqTree(CatchCase1),
      eqTo(CatchHandlerContext())
    )

    doWrite(
      """catch (IllegalStateException e2) {
        |  log.error(e2);
        |}
        |""".stripMargin)
      .when(catchHandlerTraverser).traverse(
      eqTree(CatchCase2),
      eqTo(CatchHandlerContext())
    )

    tryTraverser.traverse(`try`)

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

  test("traverse with single statement, one 'catch' case and a 'finally'") {
    val `try` = Term.Try(
      expr = TryStatement,
      catchp = List(
        CatchCase1
      ),
      finallyp = Some(FinallyStatement)
    )

    doWrite(
      """ {
        |  doSomething();
        |}
        |""".stripMargin)
      .when(blockTraverser).traverse(stat = eqTree(TryStatement), context = eqBlockContext(BlockContext()))

    doWrite(
      """catch (IllegalArgumentException e1) {
        |  log.error(e1);
        |}
        |""".stripMargin)
      .when(catchHandlerTraverser).traverse(
      eqTree(CatchCase1),
      eqTo(CatchHandlerContext())
    )

    doWrite(
      """finally {
        |  cleanup();
        |}
        |""".stripMargin)
      .when(finallyTraverser).traverse(eqTree(FinallyStatement))

    tryTraverser.traverse(`try`)

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

  test("traverse with single statement, one 'catch' case and a 'finally', and shouldReturnValue=Yes") {
    val `try` = Term.Try(
      expr = TryStatement,
      catchp = List(
        CatchCase1
      ),
      finallyp = Some(FinallyStatement)
    )

    doWrite(
      """ {
        |  return doSomething();
        |}
        |""".stripMargin)
      .when(blockTraverser).traverse(
      stat = eqTree(TryStatement),context = eqBlockContext(BlockContext(shouldReturnValue = Yes)))

    doWrite(
      """catch (IllegalArgumentException e1) {
        |  return "failed";
        |}
        |""".stripMargin)
      .when(catchHandlerTraverser).traverse(
      catchCase = eqTree(CatchCase1),
      context = eqTo(CatchHandlerContext(shouldReturnValue = Yes)))

    doWrite(
      """finally {
        |  cleanup();
        |}
        |""".stripMargin)
      .when(finallyTraverser).traverse(eqTree(FinallyStatement))

    tryTraverser.traverse(`try` = `try`, context = TryContext(shouldReturnValue = Yes))

    outputWriter.toString shouldBe
      """try {
        |  return doSomething();
        |}
        |catch (IllegalArgumentException e1) {
        |  return "failed";
        |}
        |finally {
        |  cleanup();
        |}
        |""".stripMargin
  }
}
