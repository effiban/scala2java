package io.github.effiban.scala2java.core.renderers.contextfactories

import io.github.effiban.scala2java.core.contexts.{BlockRenderContext, SimpleBlockStatRenderContext, TryRenderContext}
import io.github.effiban.scala2java.core.matchers.BlockTraversalResultMockitoMatcher
import io.github.effiban.scala2java.core.matchers.BlockTraversalResultMockitoMatcher.eqBlockTraversalResult
import io.github.effiban.scala2java.core.matchers.TryRenderContextScalatestMatcher.equalTryRenderContext
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.traversers.results.BlockTraversalResult
import io.github.effiban.scala2java.core.traversers.{TestableBlockTraversalResult, TestableTryTraversalResult, TestableTryWithHandlerTraversalResult}
import org.mockito.ArgumentMatchers.any

import scala.meta.XtensionQuasiquoteTerm

class TryRenderContextFactoryImplTest extends UnitTestSuite {
  private val blockRenderContextFactory = mock[BlockRenderContextFactory]

  private val tryRenderContextFactory = new TryRenderContextFactoryImpl(blockRenderContextFactory)

  test("apply() for a TryTraversalResult when has only an 'expr' clause") {
    val `try` =
      q"""
      try {
        doA1()
        doA2()
      }
      """

    val exprResult = TestableBlockTraversalResult(
      q"""
      {
        doA1()
        doA2()
      }
      """,
      uncertainReturn = true
    )

    val tryTraversalResult = TestableTryTraversalResult(`try`, exprUncertainReturn = true)
    val expectedExprContext = BlockRenderContext(SimpleBlockStatRenderContext(uncertainReturn = true))
    val expectedTryContext = TryRenderContext(expectedExprContext)

    doReturn(expectedExprContext).when(blockRenderContextFactory)(eqBlockTraversalResult(exprResult))

    tryRenderContextFactory(tryTraversalResult) should equalTryRenderContext(expectedTryContext)
  }

  test("apply() for a TryTraversalResult when has an 'expr' and two 'catch' clauses") {
    val `try` =
      q"""
      try {
        doA1()
        doA2()
      } catch {
        case e1: Exception1 => {
          doB1()
        }
        case e2: Exception2 => {
          doB2()
        }
      }
      """

    val exprResult = TestableBlockTraversalResult(
      q"""
      {
        doA1()
        doA2()
      }
      """,
      uncertainReturn = true
    )

    val catch1BodyResult = TestableBlockTraversalResult(
      q"""
      {
        doB1()
      }
      """,
      uncertainReturn = true
    )

    val catch2BodyResult = TestableBlockTraversalResult(
      q"""
      {
        doB2()
      }
      """,
      uncertainReturn = true
    )

    val tryTraversalResult = TestableTryTraversalResult(
      termTry = `try`,
      exprUncertainReturn = true,
      catchUncertainReturns = List(true, true)
    )
    val expectedBlockContext = BlockRenderContext(SimpleBlockStatRenderContext(uncertainReturn = true))
    val expectedTryContext = TryRenderContext(
      exprContext = expectedBlockContext,
      catchContexts = List(expectedBlockContext, expectedBlockContext)
    )

    doAnswer((result: BlockTraversalResult) => result match {
      case aResult if new BlockTraversalResultMockitoMatcher(exprResult).matches(aResult) => expectedBlockContext
      case aResult if new BlockTraversalResultMockitoMatcher(catch1BodyResult).matches(aResult) => expectedBlockContext
      case aResult if new BlockTraversalResultMockitoMatcher(catch2BodyResult).matches(aResult) => expectedBlockContext
      case _ => BlockRenderContext()
    }).when(blockRenderContextFactory)(any[BlockTraversalResult])

    tryRenderContextFactory(tryTraversalResult) should equalTryRenderContext(expectedTryContext)
  }

  test("apply() for a TryWithHandlerTraversalResult when has only an 'expr' clause") {
    val tryWithHandler =
      q"""
      try {
        doA1()
        doA2()
      } catch (catchHandler)
      """

    val exprResult = TestableBlockTraversalResult(
      q"""
      {
        doA1()
        doA2()
      }
      """,
      uncertainReturn = true
    )

    val tryWithHandlerTraversalResult = TestableTryWithHandlerTraversalResult(tryWithHandler, exprUncertainReturn = true)
    val expectedExprContext = BlockRenderContext(SimpleBlockStatRenderContext(uncertainReturn = true))
    val expectedTryContext = TryRenderContext(expectedExprContext)

    doReturn(expectedExprContext).when(blockRenderContextFactory)(eqBlockTraversalResult(exprResult))

    tryRenderContextFactory(tryWithHandlerTraversalResult) should equalTryRenderContext(expectedTryContext)
  }
}
