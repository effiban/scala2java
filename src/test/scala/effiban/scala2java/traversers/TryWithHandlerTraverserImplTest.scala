package effiban.scala2java.traversers

import effiban.scala2java.UnitTestSuite
import effiban.scala2java.matchers.TreeMatcher.eqTree
import effiban.scala2java.stubbers.OutputWriterStubber.doWrite
import org.mockito.ArgumentMatchers

import scala.meta.Term.Block
import scala.meta.{Lit, Name, Term, Type}

class TryWithHandlerTraverserImplTest extends UnitTestSuite {

  private val TryStatement = Term.Apply(Term.Name("doSomething"), Nil)

  private val TermName1: Term.Name = Term.Name("e1")
  private val TermName2: Term.Name = Term.Name("e2")

  private val TypeName1: Type.Name = Type.Name("IllegalArgumentException")
  private val TypeName2: Type.Name = Type.Name("IllegalStateException")

  private val CatchParam1 = termParam(TermName1, TypeName1)
  private val CatchParam2 = termParam(TermName2, TypeName2)

  private val CatchFunctionBody = Term.Apply(Term.Select(Term.Name("log"), Term.Name("error")), List(TermName1))
  private val SupportedCatchFunction = Term.Function(params = List(CatchParam1), body = CatchFunctionBody)
  private val UnsupportedCatchFunction = Term.Function(params = List(CatchParam1, CatchParam2), body = CatchFunctionBody)
  private val UnsupportedCatchTerm = Lit.String("unsupported")

  private val FinallyStatement = Term.Apply(Term.Name("cleanup"), Nil)


  private val blockTraverser = mock[BlockTraverser]
  private val catchHandlerTraverser = mock[CatchHandlerTraverser]
  private val finallyTraverser = mock[FinallyTraverser]

  private val tryWithHandlerTraverser = new TryWithHandlerTraverserImpl(
    blockTraverser,
    catchHandlerTraverser,
    finallyTraverser
  )

  test("traverse with a single statement, supported catch handler and no 'finally'") {
    val tryWithHandler = Term.TryWithHandler(
      expr = TryStatement,
      catchp = SupportedCatchFunction,
      finallyp = None
    )

    doWrite(
      """ {
        |  doSomething();
        |}
        |""".stripMargin)
      .when(blockTraverser).traverse(
      block = eqTree(Block(List(TryStatement))),
      shouldReturnValue = ArgumentMatchers.eq(false),
      maybeInit = ArgumentMatchers.eq(None)
    )

    doWrite(
      """catch (IllegalArgumentException e1) {
        |  log.error(e1);
        |}
        |""".stripMargin)
      .when(catchHandlerTraverser).traverse(eqTree(CatchParam1), eqTree(CatchFunctionBody))

    tryWithHandlerTraverser.traverse(tryWithHandler)

    outputWriter.toString shouldBe
      """try {
        |  doSomething();
        |}
        |catch (IllegalArgumentException e1) {
        |  log.error(e1);
        |}
        |""".stripMargin
  }

  test("traverse with a block, supported catch handler and no 'finally'") {
    val tryWithHandler = Term.TryWithHandler(
      expr = Block(List(TryStatement)),
      catchp = SupportedCatchFunction,
      finallyp = None
    )

    doWrite(
      """ {
        |  doSomething();
        |}
        |""".stripMargin)
      .when(blockTraverser).traverse(
      block = eqTree(Block(List(TryStatement))),
      shouldReturnValue = ArgumentMatchers.eq(false),
      maybeInit = ArgumentMatchers.eq(None)
    )

    doWrite(
      """catch (IllegalArgumentException e1) {
        |  log.error(e1);
        |}
        |""".stripMargin)
      .when(catchHandlerTraverser).traverse(eqTree(CatchParam1), eqTree(CatchFunctionBody))

    tryWithHandlerTraverser.traverse(tryWithHandler)

    outputWriter.toString shouldBe
      """try {
        |  doSomething();
        |}
        |catch (IllegalArgumentException e1) {
        |  log.error(e1);
        |}
        |""".stripMargin
  }

  test("traverse with single statement, supported 'catch' function and a 'finally'") {
    val tryWithHandler = Term.TryWithHandler(
      expr = TryStatement,
      catchp = SupportedCatchFunction,
      finallyp = Some(FinallyStatement)
    )

    doWrite(
      """ {
        |  doSomething();
        |}
        |""".stripMargin)
      .when(blockTraverser).traverse(
      block = eqTree(Block(List(TryStatement))),
      shouldReturnValue = ArgumentMatchers.eq(false),
      maybeInit = ArgumentMatchers.eq(None)
    )

    doWrite(
      """catch (IllegalArgumentException e1) {
        |  log.error(e1);
        |}
        |""".stripMargin)
      .when(catchHandlerTraverser).traverse(eqTree(CatchParam1), eqTree(CatchFunctionBody))

    doWrite(
      """finally {
        |  cleanup();
        |}
        |""".stripMargin)
      .when(finallyTraverser).traverse(eqTree(FinallyStatement))

    tryWithHandlerTraverser.traverse(tryWithHandler)

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

  test("traverse with a single statement, unsupported 'catch' function and no 'finally'") {
    val tryWithHandler = Term.TryWithHandler(
      expr = TryStatement,
      catchp = UnsupportedCatchFunction,
      finallyp = None
    )

    doWrite(
      """ {
        |  doSomething();
        |}
        |""".stripMargin)
      .when(blockTraverser).traverse(
      block = eqTree(Block(List(TryStatement))),
      shouldReturnValue = ArgumentMatchers.eq(false),
      maybeInit = ArgumentMatchers.eq(None)
    )

    tryWithHandlerTraverser.traverse(tryWithHandler)

    outputWriter.toString shouldBe
      """try {
        |  doSomething();
        |}
        |/* UNPARSEABLE catch handler: (e1: IllegalArgumentException, e2: IllegalStateException) => log.error(e1) */
        |""".stripMargin
  }

  test("traverse with a single statement, unsupported 'catch' term and no 'finally'") {
    val tryWithHandler = Term.TryWithHandler(
      expr = TryStatement,
      catchp = UnsupportedCatchTerm,
      finallyp = None
    )

    doWrite(
      """ {
        |  doSomething();
        |}
        |""".stripMargin)
      .when(blockTraverser).traverse(
      block = eqTree(Block(List(TryStatement))),
      shouldReturnValue = ArgumentMatchers.eq(false),
      maybeInit = ArgumentMatchers.eq(None)
    )

    tryWithHandlerTraverser.traverse(tryWithHandler)

    outputWriter.toString shouldBe
      """try {
        |  doSomething();
        |}
        |/* UNPARSEABLE catch handler: "unsupported" */
        |""".stripMargin
  }

  private def termParam(name: Name, decltpe: Type): Term.Param = {
    Term.Param(mods = Nil, name = name, decltpe = Some(decltpe), default = None)
  }
}
