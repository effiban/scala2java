package effiban.scala2java.traversers

import effiban.scala2java.matchers.TreeMatcher.eqTree
import effiban.scala2java.stubbers.OutputWriterStubber.doWrite
import effiban.scala2java.testsuites.UnitTestSuite
import effiban.scala2java.transformers.PatToTermParamTransformer
import org.mockito.ArgumentMatchers
import org.mockito.ArgumentMatchers.any

import scala.meta.Term.{Apply, Block}
import scala.meta.{Case, Name, Pat, Term, Type}

class TryTraverserImplTest extends UnitTestSuite {

  private val TryStatement = Term.Apply(Term.Name("doSomething"), Nil)

  private val TermName1: Term.Name = Term.Name("e1")
  private val TermName2: Term.Name = Term.Name("e2")

  private val TypeName1: Type.Name = Type.Name("IllegalArgumentException")
  private val TypeName2: Type.Name = Type.Name("IllegalStateException")

  private val CatchPat1 = Pat.Typed(TermName1, TypeName1)
  private val CatchPat2 = Pat.Typed(TermName2, TypeName2)
  private val UnsupportedCatchPat = Pat.Repeated(Term.Name("e"))

  private val CatchParam1 = termParam(TermName1, TypeName1)
  private val CatchParam2 = termParam(TermName2, TypeName2)

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


  private val blockTraverser = mock[BlockTraverser]
  private val catchHandlerTraverser = mock[CatchHandlerTraverser]
  private val finallyTraverser = mock[FinallyTraverser]
  private val patToTermParamTransformer = mock[PatToTermParamTransformer]

  private val tryTraverser = new TryTraverserImpl(
    blockTraverser,
    catchHandlerTraverser,
    finallyTraverser,
    patToTermParamTransformer
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
      .when(blockTraverser).traverse(
      block = eqTree(Block(List(TryStatement))),
      shouldReturnValue = ArgumentMatchers.eq(false),
      maybeInit = ArgumentMatchers.eq(None)
    )

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
      .when(blockTraverser).traverse(
      block = eqTree(Block(List(TryStatement))),
      shouldReturnValue = ArgumentMatchers.eq(false),
      maybeInit = ArgumentMatchers.eq(None)
    )

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
      .when(blockTraverser).traverse(
      block = eqTree(Block(List(TryStatement))),
      shouldReturnValue = ArgumentMatchers.eq(false),
      maybeInit = ArgumentMatchers.eq(None)
    )

    when(patToTermParamTransformer.transform(eqTree(CatchPat1))).thenReturn(Some(CatchParam1))

    doWrite(
      """catch (IllegalArgumentException e1) {
        |  log.error(e1);
        |}
        |""".stripMargin)
      .when(catchHandlerTraverser).traverse(eqTree(CatchParam1), eqTree(CatchStatement1))

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
      .when(blockTraverser).traverse(
      block = eqTree(Block(List(TryStatement))),
      shouldReturnValue = ArgumentMatchers.eq(false),
      maybeInit = ArgumentMatchers.eq(None)
    )

    when(patToTermParamTransformer.transform(any[Pat]))
      .thenAnswer( (pat: Pat) => {
        pat match {
          case aPat if aPat.structure == CatchPat1.structure => Some(CatchParam1)
          case aPat if aPat.structure == CatchPat2.structure => Some(CatchParam2)
        }
      })

    doWrite(
      """catch (IllegalArgumentException e1) {
        |  log.error(e1);
        |}
        |""".stripMargin)
      .when(catchHandlerTraverser).traverse(eqTree(CatchParam1), eqTree(CatchStatement1))
    doWrite(
      """catch (IllegalStateException e2) {
        |  log.error(e2);
        |}
        |""".stripMargin)
      .when(catchHandlerTraverser).traverse(eqTree(CatchParam2), eqTree(CatchStatement2))

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
      .when(blockTraverser).traverse(
      block = eqTree(Block(List(TryStatement))),
      shouldReturnValue = ArgumentMatchers.eq(false),
      maybeInit = ArgumentMatchers.eq(None)
    )

    when(patToTermParamTransformer.transform(eqTree(CatchPat1))).thenReturn(Some(CatchParam1))

    doWrite(
      """catch (IllegalArgumentException e1) {
        |  log.error(e1);
        |}
        |""".stripMargin)
      .when(catchHandlerTraverser).traverse(eqTree(CatchParam1), eqTree(CatchStatement1))

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

  test("traverse with two 'catch' cases when first is unsupported") {
    val `try` = Term.Try(
      expr = TryStatement,
      catchp = List(
        UnsupportedCatchCase,
        CatchCase2
      ),
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

    when(patToTermParamTransformer.transform(any[Pat]))
      .thenAnswer( (pat: Pat) => {
        pat match {
          case aPat if aPat.structure == UnsupportedCatchPat.structure => None
          case aPat if aPat.structure == CatchPat2.structure => Some(CatchParam2)
        }
      })

    doWrite(
      """catch (IllegalStateException e2) {
        |  log.error(e2);
        |}
        |""".stripMargin)
      .when(catchHandlerTraverser).traverse(eqTree(CatchParam2), eqTree(CatchStatement2))

    tryTraverser.traverse(`try`)

    outputWriter.toString shouldBe
      """try {
        |  doSomething();
        |}
        |/**
        |* UNPARSEABLE catch clause: case e* =>
        |*   log.error(e)
        |*/
        |catch (IllegalStateException e2) {
        |  log.error(e2);
        |}
        |""".stripMargin
  }

  test("traverse with two 'catch' cases when second is unsupported") {
    val `try` = Term.Try(
      expr = TryStatement,
      catchp = List(
        CatchCase1,
        UnsupportedCatchCase
      ),
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

    when(patToTermParamTransformer.transform(any[Pat]))
      .thenAnswer( (pat: Pat) => {
        pat match {
          case aPat if aPat.structure == CatchPat1.structure => Some(CatchParam1)
          case aPat if aPat.structure == UnsupportedCatchPat.structure => None
        }
      })

    doWrite(
      """catch (IllegalStateException e1) {
        |  log.error(e1);
        |}
        |""".stripMargin)
      .when(catchHandlerTraverser).traverse(eqTree(CatchParam1), eqTree(CatchStatement1))

    tryTraverser.traverse(`try`)

    outputWriter.toString shouldBe
      """try {
        |  doSomething();
        |}
        |catch (IllegalStateException e1) {
        |  log.error(e1);
        |}
        |/**
        |* UNPARSEABLE catch clause: case e* =>
        |*   log.error(e)
        |*/
        |""".stripMargin
  }

  private def termParam(name: Name, decltpe: Type): Term.Param = {
    Term.Param(mods = Nil, name = name, decltpe = Some(decltpe), default = None)
  }
}
