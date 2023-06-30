package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.{BlockContext, CatchHandlerContext, TryContext}
import io.github.effiban.scala2java.core.entities.Decision.{Uncertain, Yes}
import io.github.effiban.scala2java.core.matchers.BlockContextMatcher.eqBlockContext
import io.github.effiban.scala2java.core.matchers.TryTraversalResultScalatestMatcher.equalTryTraversalResult
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchersSugar.{any, eqTo}

import scala.meta.Term.Block
import scala.meta.{Case, Term, XtensionQuasiquoteCaseOrPattern, XtensionQuasiquoteTerm}

class TryTraverserImplTest extends UnitTestSuite {

  private val TryExprStatement = q"doSomething()"
  private val TryExprBlock =
    q"""
    {
      doSomething()
    }
    """
  private val TraversedTryExprBlock =
    q"""
    {
      doSomething2()
    }
    """

  private val CatchPat1 = p"e1: IllegalArgumentException"
  private val TraversedCatchPat1 = p"e11: IllegalArgumentException"
  private val CatchPat2 = p"e2: IllegalStateException"
  private val TraversedCatchPat2 = p"e22: IllegalStateException"

  private val CatchCase1 = Case(
    pat = CatchPat1,
    cond = None,
    body = q"log.error(e1)"
  )
  private val TraversedCatchCase1 = Case(
    pat = TraversedCatchPat1,
    cond = None,
    body =
      q"""
      {
        log.error(e11)
      }
      """
  )

  private val CatchCase2 = Case(
    pat = CatchPat2,
    cond = None,
    body = q"log.error(e2)"
  )
  private val TraversedCatchCase2 = Case(
    pat = TraversedCatchPat2,
    cond = None,
    body =
      q"""
      {
        log.error(e22)
      }
      """
  )

  private val FinallyStatement = q"cleanup()"
  private val FinallyBlock =
    q"""
    {
      cleanup()
    }
    """
  private val TraversedFinallyBlock =
    q"""
    {
      cleanup2()
    }
    """

  private val blockWrappingTermTraverser = mock[BlockWrappingTermTraverser]
  private val catchHandlerTraverser = mock[CatchHandlerTraverser]
  private val finallyTraverser = mock[FinallyTraverser]

  private val tryTraverser = new TryTraverserImpl(
    blockWrappingTermTraverser,
    catchHandlerTraverser,
    finallyTraverser
  )

  test("traverse with a statement expr, no 'catch' cases and no 'finally'") {
    val `try` = Term.Try(
      expr = TryExprStatement,
      catchp = Nil,
      finallyp = None
    )
    val traversedTry = Term.Try(
      expr = TraversedTryExprBlock,
      catchp = Nil,
      finallyp = None
    )
    val expectedResult = TestableTryTraversalResult(traversedTry)

    doReturn(TestableBlockTraversalResult(TraversedTryExprBlock))
      .when(blockWrappingTermTraverser).traverse(term = eqTree(TryExprStatement), context = eqBlockContext(BlockContext()))

    tryTraverser.traverse(`try`) should equalTryTraversalResult(expectedResult)
  }

  test("traverse with a statement expr") {
    val `try` = Term.Try(
      expr = TryExprStatement,
      catchp = Nil,
      finallyp = None
    )
    val traversedTry = Term.Try(
      expr = TraversedTryExprBlock,
      catchp = Nil,
      finallyp = None
    )
    val expectedResult = TestableTryTraversalResult(traversedTry)

    doReturn(TestableBlockTraversalResult(TraversedTryExprBlock))
      .when(blockWrappingTermTraverser).traverse(term = eqTree(TryExprStatement), context = eqBlockContext(BlockContext()))

    tryTraverser.traverse(`try`) should equalTryTraversalResult(expectedResult)
  }

  test("traverse with a statement expr, shouldReturnValue=Uncertain and output uncertainReturn=true ") {
    val `try` = Term.Try(
      expr = TryExprStatement,
      catchp = Nil,
      finallyp = None
    )
    val traversedTry = Term.Try(
      expr = TraversedTryExprBlock,
      catchp = Nil,
      finallyp = None
    )
    val tryContext = TryContext(shouldReturnValue = Uncertain)
    val expectedExprContext = BlockContext(shouldReturnValue = Uncertain)
    val expectedExprResult = TestableBlockTraversalResult(TraversedTryExprBlock, uncertainReturn = true)
    val expectedTryResult = TestableTryTraversalResult(traversedTry, exprUncertainReturn = true)

    doReturn(expectedExprResult)
      .when(blockWrappingTermTraverser).traverse(term = eqTree(TryExprStatement), context = eqBlockContext(expectedExprContext))

    tryTraverser.traverse(`try`, tryContext) should equalTryTraversalResult(expectedTryResult)
  }

  test("traverse with a block expr") {
    val `try` = Term.Try(
      expr = TryExprBlock,
      catchp = Nil,
      finallyp = None
    )
    val traversedTry = Term.Try(
      expr = TraversedTryExprBlock,
      catchp = Nil,
      finallyp = None
    )
    val expectedResult = TestableTryTraversalResult(traversedTry)

    doReturn(TestableBlockTraversalResult(TraversedTryExprBlock))
      .when(blockWrappingTermTraverser).traverse(term = eqTree(Block(List(TryExprStatement))), context = eqBlockContext(BlockContext()))

    tryTraverser.traverse(`try`) should equalTryTraversalResult(expectedResult)
  }

  test("traverse with statement expr and one 'catch' case") {
    val `try` = Term.Try(
      expr = TryExprStatement,
      catchp = List(CatchCase1),
      finallyp = None
    )
    val traversedTry = Term.Try(
      expr = TraversedTryExprBlock,
      catchp = List(TraversedCatchCase1),
      finallyp = None
    )
    val expectedResult = TestableTryTraversalResult(traversedTry, catchUncertainReturns = List(false))

    doReturn(TestableBlockTraversalResult(TraversedTryExprBlock))
      .when(blockWrappingTermTraverser).traverse(term = eqTree(TryExprStatement),context = eqBlockContext(BlockContext()))
    doReturn(TestableCatchHandlerTraversalResult(TraversedCatchCase1))
      .when(catchHandlerTraverser).traverse(eqTree(CatchCase1), eqTo(CatchHandlerContext()))

    tryTraverser.traverse(`try`) should equalTryTraversalResult(expectedResult)
  }

  test("traverse with a statement expr and two 'catch' cases") {
    val `try` = Term.Try(
      expr = TryExprStatement,
      catchp = List(CatchCase1, CatchCase2),
      finallyp = None
    )
    val traversedTry = Term.Try(
      expr = TraversedTryExprBlock,
      catchp = List(TraversedCatchCase1, TraversedCatchCase2),
      finallyp = None
    )
    val expectedResult = TestableTryTraversalResult(traversedTry, catchUncertainReturns = List(false, false))

    doReturn(TestableBlockTraversalResult(TraversedTryExprBlock))
      .when(blockWrappingTermTraverser).traverse(term = eqTree(TryExprStatement), context = eqBlockContext(BlockContext()))

    doAnswer((catchCase: Case) => catchCase match {
      case aCatchCase if aCatchCase.structure == CatchCase1.structure => TestableCatchHandlerTraversalResult(TraversedCatchCase1)
      case aCatchCase if aCatchCase.structure == CatchCase2.structure => TestableCatchHandlerTraversalResult(TraversedCatchCase2)
      case aCatchCase => aCatchCase
    }).when(catchHandlerTraverser).traverse(any[Case], eqTo(CatchHandlerContext()))

    tryTraverser.traverse(`try`) should equalTryTraversalResult(expectedResult)
  }

  test("traverse with a statement expr and two 'catch' cases, shouldReturnValue=Uncertain and all results have uncertainReturn=true") {
    val `try` = Term.Try(
      expr = TryExprStatement,
      catchp = List(CatchCase1, CatchCase2),
      finallyp = None
    )
    val traversedTry = Term.Try(
      expr = TraversedTryExprBlock,
      catchp = List(TraversedCatchCase1, TraversedCatchCase2),
      finallyp = None
    )
    val tryContext = TryContext(shouldReturnValue = Uncertain)
    val expectedExprContext = BlockContext(shouldReturnValue = Uncertain)
    val expectedCatchHandlerContext = CatchHandlerContext(shouldReturnValue = Uncertain)
    val expectedExprResult = TestableBlockTraversalResult(TraversedTryExprBlock, uncertainReturn = true)
    val expectedCatchCase1Result = TestableCatchHandlerTraversalResult(TraversedCatchCase1, uncertainReturn = true)
    val expectedCatchCase2Result = TestableCatchHandlerTraversalResult(TraversedCatchCase2, uncertainReturn = true)
    val expectedTryResult = TestableTryTraversalResult(traversedTry, exprUncertainReturn = true, catchUncertainReturns = List(true, true))

    doReturn(expectedExprResult)
      .when(blockWrappingTermTraverser).traverse(term = eqTree(TryExprStatement), context = eqBlockContext(expectedExprContext))

    doAnswer((catchCase: Case) => catchCase match {
      case aCatchCase if aCatchCase.structure == CatchCase1.structure => expectedCatchCase1Result
      case aCatchCase if aCatchCase.structure == CatchCase2.structure => expectedCatchCase2Result
      case aCatchCase => aCatchCase
    }).when(catchHandlerTraverser).traverse(any[Case], eqTo(expectedCatchHandlerContext))

    tryTraverser.traverse(`try`, tryContext) should equalTryTraversalResult(expectedTryResult)
  }

  test("traverse with a statement expr and two 'catch' cases, shouldReturnValue=Uncertain and all but second 'catch' have uncertainReturn=true") {
    val `try` = Term.Try(
      expr = TryExprStatement,
      catchp = List(CatchCase1, CatchCase2),
      finallyp = None
    )
    val traversedTry = Term.Try(
      expr = TraversedTryExprBlock,
      catchp = List(TraversedCatchCase1, TraversedCatchCase2),
      finallyp = None
    )
    val tryContext = TryContext(shouldReturnValue = Uncertain)
    val expectedExprContext = BlockContext(shouldReturnValue = Uncertain)
    val expectedCatchHandlerContext = CatchHandlerContext(shouldReturnValue = Uncertain)
    val expectedExprResult = TestableBlockTraversalResult(TraversedTryExprBlock, uncertainReturn = true)
    val expectedCatchCase1Result = TestableCatchHandlerTraversalResult(TraversedCatchCase1, uncertainReturn = true)
    val expectedCatchCase2Result = TestableCatchHandlerTraversalResult(TraversedCatchCase2)
    val expectedTryResult = TestableTryTraversalResult(traversedTry, exprUncertainReturn = true, catchUncertainReturns = List(true, false))

    doReturn(expectedExprResult)
      .when(blockWrappingTermTraverser).traverse(term = eqTree(TryExprStatement), context = eqBlockContext(expectedExprContext))

    doAnswer((catchCase: Case) => catchCase match {
      case aCatchCase if aCatchCase.structure == CatchCase1.structure => expectedCatchCase1Result
      case aCatchCase if aCatchCase.structure == CatchCase2.structure => expectedCatchCase2Result
      case aCatchCase => aCatchCase
    }).when(catchHandlerTraverser).traverse(any[Case], eqTo(expectedCatchHandlerContext))

    tryTraverser.traverse(`try`, tryContext) should equalTryTraversalResult(expectedTryResult)
  }

  test("traverse with a statement expr, one 'catch' case and a 'finally' statement") {
    val `try` = Term.Try(
      expr = TryExprStatement,
      catchp = List(CatchCase1),
      finallyp = Some(FinallyStatement)
    )
    val traversedTry = Term.Try(
      expr = TraversedTryExprBlock,
      catchp = List(TraversedCatchCase1),
      finallyp = Some(TraversedFinallyBlock)
    )
    val expectedResult = TestableTryTraversalResult(traversedTry, catchUncertainReturns = List(false))

    doReturn(TestableBlockTraversalResult(TraversedTryExprBlock))
      .when(blockWrappingTermTraverser).traverse(term = eqTree(TryExprStatement), context = eqBlockContext(BlockContext()))
    doReturn(TestableCatchHandlerTraversalResult(TraversedCatchCase1))
      .when(catchHandlerTraverser).traverse(eqTree(CatchCase1), eqTo(CatchHandlerContext()))
    doReturn(TraversedFinallyBlock).when(finallyTraverser).traverse(eqTree(FinallyStatement))

    tryTraverser.traverse(`try`) should equalTryTraversalResult(expectedResult)
  }

  test("traverse with a statement expr, one 'catch' case and a 'finally' block") {
    val `try` = Term.Try(
      expr = TryExprStatement,
      catchp = List(CatchCase1),
      finallyp = Some(FinallyBlock)
    )
    val traversedTry = Term.Try(
      expr = TraversedTryExprBlock,
      catchp = List(TraversedCatchCase1),
      finallyp = Some(TraversedFinallyBlock)
    )
    val expectedResult = TestableTryTraversalResult(traversedTry, catchUncertainReturns = List(false))

    doReturn(TestableBlockTraversalResult(TraversedTryExprBlock))
      .when(blockWrappingTermTraverser).traverse(term = eqTree(TryExprStatement), context = eqBlockContext(BlockContext()))
    doReturn(TestableCatchHandlerTraversalResult(TraversedCatchCase1))
      .when(catchHandlerTraverser).traverse(eqTree(CatchCase1), eqTo(CatchHandlerContext()))
    doReturn(TraversedFinallyBlock).when(finallyTraverser).traverse(eqTree(FinallyBlock))

    tryTraverser.traverse(`try`) should equalTryTraversalResult(expectedResult)
  }

  test("traverse with a statement expr, one 'catch' case and a 'finally', and shouldReturnValue=Yes") {
    val `try` = Term.Try(
      expr = TryExprStatement,
      catchp = List(CatchCase1),
      finallyp = Some(FinallyStatement)
    )
    val traversedTry = Term.Try(
      expr = TraversedTryExprBlock,
      catchp = List(TraversedCatchCase1),
      finallyp = Some(TraversedFinallyBlock)
    )
    val expectedResult = TestableTryTraversalResult(traversedTry, catchUncertainReturns = List(false))

    doReturn(TestableBlockTraversalResult(TraversedTryExprBlock)).when(blockWrappingTermTraverser).traverse(
      term = eqTree(TryExprStatement),
      context = eqBlockContext(BlockContext(shouldReturnValue = Yes)))

    doReturn(TestableCatchHandlerTraversalResult(TraversedCatchCase1)).when(catchHandlerTraverser).traverse(
      catchCase = eqTree(CatchCase1),
      context = eqTo(CatchHandlerContext(shouldReturnValue = Yes)))

    doReturn(TraversedFinallyBlock).when(finallyTraverser).traverse(eqTree(FinallyStatement))

    val actualResult = tryTraverser.traverse(`try` = `try`, context = TryContext(shouldReturnValue = Yes))
    actualResult should equalTryTraversalResult(expectedResult)
  }
}
