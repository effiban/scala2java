package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.{BlockContext, TryContext}
import io.github.effiban.scala2java.core.entities.Decision.{Uncertain, Yes}
import io.github.effiban.scala2java.core.matchers.BlockContextMatcher.eqBlockContext
import io.github.effiban.scala2java.core.matchers.TryWithHandlerTraversalResultScalatestMatcher.equalTryWithHandlerTraversalResult
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.Term.{Block, TryWithHandler}
import scala.meta.XtensionQuasiquoteTerm

class TryWithHandlerTraverserImplTest extends UnitTestSuite {

  private val TryExprStatement = q"doSomething()"
  private val TryExprBlock =
    q"""
    {
      doSomething()
    }
    """
  private val traversedTryWithHandlerExprBlock =
    q"""
    {
      doSomething2()
    }
    """

  private val CatchHandler = q"handler"

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
  private val finallyTraverser = mock[FinallyTraverser]

  private val tryWithHandlerTraverser = new TryWithHandlerTraverserImpl(
    blockWrappingTermTraverser,
    finallyTraverser
  )

  test("traverse with a statement expr") {
    val tryWithHandler = TryWithHandler(
      expr = TryExprStatement,
      catchp = CatchHandler,
      finallyp = None
    )
    val traversedTryWithHandler = TryWithHandler(
      expr = traversedTryWithHandlerExprBlock,
      catchp = CatchHandler,
      finallyp = None
    )
    val expectedResult = TestableTryWithHandlerTraversalResult(traversedTryWithHandler)

    doReturn(TestableBlockTraversalResult(traversedTryWithHandlerExprBlock))
      .when(blockWrappingTermTraverser).traverse(term = eqTree(TryExprStatement), context = eqBlockContext(BlockContext()))

    tryWithHandlerTraverser.traverse(tryWithHandler) should equalTryWithHandlerTraversalResult(expectedResult)
  }

  test("traverse with a statement expr and shouldReturnValue=Yes") {
    val tryWithHandler = TryWithHandler(
      expr = TryExprStatement,
      catchp = CatchHandler,
      finallyp = None
    )
    val traversedTryWithHandler = TryWithHandler(
      expr = traversedTryWithHandlerExprBlock,
      catchp = CatchHandler,
      finallyp = None
    )
    val expectedBlockContext = BlockContext(shouldReturnValue = Yes)
    val expectedResult = TestableTryWithHandlerTraversalResult(traversedTryWithHandler)

    doReturn(TestableBlockTraversalResult(traversedTryWithHandlerExprBlock))
      .when(blockWrappingTermTraverser).traverse(term = eqTree(TryExprStatement), context = eqBlockContext(expectedBlockContext))

    tryWithHandlerTraverser.traverse(tryWithHandler, TryContext(shouldReturnValue = Yes)) should equalTryWithHandlerTraversalResult(expectedResult)
  }
  
  test("traverse with a statement expr, shouldReturnValue=Uncertain and output uncertainReturn=true ") {
    val tryWithHandler = TryWithHandler(
      expr = TryExprStatement,
      catchp = CatchHandler,
      finallyp = None
    )
    val traversedTryWithHandler = TryWithHandler(
      expr = traversedTryWithHandlerExprBlock,
      catchp = CatchHandler,
      finallyp = None
    )
    val tryContext = TryContext(shouldReturnValue = Uncertain)
    val expectedExprContext = BlockContext(shouldReturnValue = Uncertain)
    val expectedExprResult = TestableBlockTraversalResult(traversedTryWithHandlerExprBlock, uncertainReturn = true)
    val expectedTryResult = TestableTryWithHandlerTraversalResult(traversedTryWithHandler, exprUncertainReturn = true)

    doReturn(expectedExprResult)
      .when(blockWrappingTermTraverser).traverse(term = eqTree(TryExprStatement), context = eqBlockContext(expectedExprContext))

    tryWithHandlerTraverser.traverse(tryWithHandler, tryContext) should equalTryWithHandlerTraversalResult(expectedTryResult)
  }

  test("traverse with a block expr") {
    val tryWithHandler = TryWithHandler(
      expr = TryExprBlock,
      catchp = CatchHandler,
      finallyp = None
    )
    val traversedTryWithHandler = TryWithHandler(
      expr = traversedTryWithHandlerExprBlock,
      catchp = CatchHandler,
      finallyp = None
    )
    val expectedResult = TestableTryWithHandlerTraversalResult(traversedTryWithHandler)

    doReturn(TestableBlockTraversalResult(traversedTryWithHandlerExprBlock))
      .when(blockWrappingTermTraverser).traverse(term = eqTree(Block(List(TryExprStatement))), context = eqBlockContext(BlockContext()))

    tryWithHandlerTraverser.traverse(tryWithHandler) should equalTryWithHandlerTraversalResult(expectedResult)
  }

  test("traverse with a statement expr and a 'finally' statement") {
    val tryWithHandler = TryWithHandler(
      expr = TryExprStatement,
      catchp = CatchHandler,
      finallyp = Some(FinallyStatement)
    )
    val traversedTryWithHandler = TryWithHandler(
      expr = traversedTryWithHandlerExprBlock,
      catchp = CatchHandler,
      finallyp = Some(TraversedFinallyBlock)
    )
    val expectedResult = TestableTryWithHandlerTraversalResult(traversedTryWithHandler)

    doReturn(TestableBlockTraversalResult(traversedTryWithHandlerExprBlock))
      .when(blockWrappingTermTraverser).traverse(term = eqTree(TryExprStatement), context = eqBlockContext(BlockContext()))
    doReturn(TraversedFinallyBlock).when(finallyTraverser).traverse(eqTree(FinallyStatement))

    tryWithHandlerTraverser.traverse(tryWithHandler) should equalTryWithHandlerTraversalResult(expectedResult)
  }

  test("traverse with a statement expr and a 'finally' block") {
    val tryWithHandler = TryWithHandler(
      expr = TryExprStatement,
      catchp = CatchHandler,
      finallyp = Some(FinallyBlock)
    )
    val traversedTryWithHandler = TryWithHandler(
      expr = traversedTryWithHandlerExprBlock,
      catchp = CatchHandler,
      finallyp = Some(TraversedFinallyBlock)
    )
    val expectedResult = TestableTryWithHandlerTraversalResult(traversedTryWithHandler)

    doReturn(TestableBlockTraversalResult(traversedTryWithHandlerExprBlock))
      .when(blockWrappingTermTraverser).traverse(term = eqTree(TryExprStatement), context = eqBlockContext(BlockContext()))
    doReturn(TraversedFinallyBlock).when(finallyTraverser).traverse(eqTree(FinallyBlock))

    tryWithHandlerTraverser.traverse(tryWithHandler) should equalTryWithHandlerTraversalResult(expectedResult)
  }
}
