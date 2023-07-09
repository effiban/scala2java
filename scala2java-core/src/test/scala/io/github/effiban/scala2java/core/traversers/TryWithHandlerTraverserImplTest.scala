package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.{BlockContext, TryContext}
import io.github.effiban.scala2java.core.entities.Decision.{Uncertain, Yes}
import io.github.effiban.scala2java.core.matchers.BlockContextMatcher.eqBlockContext
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

    doReturn(traversedTryWithHandlerExprBlock)
      .when(blockWrappingTermTraverser).traverse(term = eqTree(TryExprStatement), context = eqBlockContext(BlockContext()))

    tryWithHandlerTraverser.traverse(tryWithHandler).structure shouldBe traversedTryWithHandler.structure
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

    doReturn(traversedTryWithHandlerExprBlock)
      .when(blockWrappingTermTraverser).traverse(term = eqTree(TryExprStatement), context = eqBlockContext(expectedBlockContext))

    val actualResult = tryWithHandlerTraverser.traverse(tryWithHandler, TryContext(shouldReturnValue = Yes))
    actualResult.structure shouldBe traversedTryWithHandler.structure
  }
  
  test("traverse with a statement expr, shouldReturnValue=Uncertain") {
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
    val expectedExprResult = traversedTryWithHandlerExprBlock

    doReturn(expectedExprResult)
      .when(blockWrappingTermTraverser).traverse(term = eqTree(TryExprStatement), context = eqBlockContext(expectedExprContext))

    tryWithHandlerTraverser.traverse(tryWithHandler, tryContext).structure shouldBe traversedTryWithHandler.structure
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

    doReturn(traversedTryWithHandlerExprBlock)
      .when(blockWrappingTermTraverser).traverse(term = eqTree(Block(List(TryExprStatement))), context = eqBlockContext(BlockContext()))

    tryWithHandlerTraverser.traverse(tryWithHandler).structure shouldBe traversedTryWithHandler.structure
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

    doReturn(traversedTryWithHandlerExprBlock)
      .when(blockWrappingTermTraverser).traverse(term = eqTree(TryExprStatement), context = eqBlockContext(BlockContext()))
    doReturn(TraversedFinallyBlock).when(finallyTraverser).traverse(eqTree(FinallyStatement))

    tryWithHandlerTraverser.traverse(tryWithHandler).structure shouldBe traversedTryWithHandler.structure
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

    doReturn(traversedTryWithHandlerExprBlock)
      .when(blockWrappingTermTraverser).traverse(term = eqTree(TryExprStatement), context = eqBlockContext(BlockContext()))
    doReturn(TraversedFinallyBlock).when(finallyTraverser).traverse(eqTree(FinallyBlock))

    tryWithHandlerTraverser.traverse(tryWithHandler).structure shouldBe traversedTryWithHandler.structure
  }
}
