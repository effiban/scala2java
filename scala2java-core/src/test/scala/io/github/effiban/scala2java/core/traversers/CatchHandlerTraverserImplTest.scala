package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.{BlockContext, CatchHandlerContext}
import io.github.effiban.scala2java.core.entities.Decision.{Uncertain, Yes}
import io.github.effiban.scala2java.core.matchers.BlockContextMatcher.eqBlockContext
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.{Case, XtensionQuasiquoteCaseOrPattern, XtensionQuasiquoteTerm}

class CatchHandlerTraverserImplTest extends UnitTestSuite {

  private val CatchArg = p"e"
  private val TraversedCatchArg = p"e: RuntimeException"

  private val Body = q"log.error(e)"
  private val TraversedBody =
    q"""
    {
      log.error(ee)
    }
    """

  private val CatchCase = Case(
    pat = CatchArg,
    cond = None,
    body = Body
  )

  private val TraversedCatchCase = Case(
    pat = TraversedCatchArg,
    cond = None,
    body = TraversedBody
  )

  private val catchArgumentTraverser = mock[CatchArgumentTraverser]
  private val blockWrappingTermTraverser = mock[BlockWrappingTermTraverser]

  private val catchHandlerTraverser = new CatchHandlerTraverserImpl(
    catchArgumentTraverser,
    blockWrappingTermTraverser
  )

  test("traverse() when shouldReturnValue=No") {
    val expectedBlockTraversalResult = TraversedBody

    doReturn(TraversedCatchArg).when(catchArgumentTraverser).traverse(eqTree(CatchArg))

    doReturn(expectedBlockTraversalResult).when(blockWrappingTermTraverser).traverse(
      term = eqTree(Body),
      context = eqBlockContext(BlockContext())
    )

    catchHandlerTraverser.traverse(CatchCase).structure shouldBe TraversedCatchCase.structure
  }

  test("traverse() when shouldReturnValue=Yes") {
    val catchHandlerContext = CatchHandlerContext(shouldReturnValue = Yes)
    val expectedBlockContext = BlockContext(shouldReturnValue = Yes)

    val expectedBlockTraversalResult = TraversedBody

    doReturn(TraversedCatchArg).when(catchArgumentTraverser).traverse(eqTree(CatchArg))

    doReturn(expectedBlockTraversalResult)
      .when(blockWrappingTermTraverser).traverse(term = eqTree(Body), context = eqBlockContext(expectedBlockContext))

    val actualCatchHandlerResult = catchHandlerTraverser.traverse(CatchCase, catchHandlerContext)
    actualCatchHandlerResult.structure shouldBe TraversedCatchCase.structure
  }

  test("traverse() when shouldReturnValue=Uncertain") {
    val catchHandlerContext = CatchHandlerContext(shouldReturnValue = Uncertain)
    val expectedBlockContext = BlockContext(shouldReturnValue = Uncertain)

    val expectedBlockTraversalResult = TraversedBody

    doReturn(TraversedCatchArg).when(catchArgumentTraverser).traverse(eqTree(CatchArg))

    doReturn(expectedBlockTraversalResult)
      .when(blockWrappingTermTraverser).traverse(term = eqTree(Body), context = eqBlockContext(expectedBlockContext))

    val actualCatchHandlerResult = catchHandlerTraverser.traverse(CatchCase, catchHandlerContext)
    actualCatchHandlerResult.structure shouldBe TraversedCatchCase.structure
  }
}
