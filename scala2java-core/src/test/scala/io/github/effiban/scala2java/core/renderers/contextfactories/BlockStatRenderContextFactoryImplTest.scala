package io.github.effiban.scala2java.core.renderers.contextfactories

import io.github.effiban.scala2java.core.contexts.{BlockRenderContext, IfRenderContext, SimpleBlockStatRenderContext, TryRenderContext}
import io.github.effiban.scala2java.core.matchers.BlockStatRenderContextScalatestMatcher.equalBlockStatRenderContext
import io.github.effiban.scala2java.core.matchers.IfTraversalResultMockitoMatcher.eqIfTraversalResult
import io.github.effiban.scala2java.core.matchers.TryTraversalResultMockitoMatcher.eqTryTraversalResult
import io.github.effiban.scala2java.core.matchers.TryWithHandlerTraversalResultMockitoMatcher.eqTryWithHandlerTraversalResult
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.traversers.results.SimpleBlockStatTraversalResult
import io.github.effiban.scala2java.core.traversers.{TestableIfTraversalResult, TestableTryTraversalResult, TestableTryWithHandlerTraversalResult}

import scala.meta.XtensionQuasiquoteTerm

class BlockStatRenderContextFactoryImplTest extends UnitTestSuite {

  private val ifRenderContextFactory = mock[IfRenderContextFactory]
  private val tryRenderContextFactory = mock[TryRenderContextFactory]

  private val blockStatRenderContextFactory = new BlockStatRenderContextFactoryImpl(
    ifRenderContextFactory,
    tryRenderContextFactory
  )

  test("apply() for a SimpleBlockStatTraversalResult with uncertainReturn=false") {
    val traversalResult = SimpleBlockStatTraversalResult(q"dummy")
    blockStatRenderContextFactory(traversalResult) shouldBe SimpleBlockStatRenderContext()
  }

  test("apply() for a SimpleBlockStatTraversalResult with uncertainReturn=true") {
    val traversalResult = SimpleBlockStatTraversalResult(q"dummy", uncertainReturn = true)
    blockStatRenderContextFactory(traversalResult) shouldBe SimpleBlockStatRenderContext(uncertainReturn = true)
  }

  test("apply() for an IfTraversalResult") {
    val `if` =
      q"""
      if (x < 3) {
        doA()
      } else {
        doB()
      }
      """

    val ifTraversalResult = TestableIfTraversalResult(`if`, thenUncertainReturn = true, elseUncertainReturn = true)
    val expectedClauseContext = BlockRenderContext(SimpleBlockStatRenderContext(uncertainReturn = true))
    val expectedIfContext = IfRenderContext(thenContext = expectedClauseContext, elseContext = expectedClauseContext)

    doReturn(expectedIfContext).when(ifRenderContextFactory)(eqIfTraversalResult(ifTraversalResult))

    blockStatRenderContextFactory(ifTraversalResult) should equalBlockStatRenderContext(expectedIfContext)
  }

  test("apply() for a TryTraversalResult") {
    val `try` =
      q"""
      try {
        doA()
      } catch {
        case e: Throwable => {
          logError()
        }
      }
      """

    val tryTraversalResult = TestableTryTraversalResult(`try`, exprUncertainReturn = true, catchUncertainReturns = List(true))
    val expectedClauseContext = BlockRenderContext(SimpleBlockStatRenderContext(uncertainReturn = true))
    val expectedTryContext = TryRenderContext(exprContext = expectedClauseContext, catchContexts = List(expectedClauseContext))

    doReturn(expectedTryContext).when(tryRenderContextFactory)(eqTryTraversalResult(tryTraversalResult))

    blockStatRenderContextFactory(tryTraversalResult) should equalBlockStatRenderContext(expectedTryContext)
  }

  test("apply() for a TryWithHandlerTraversalResult") {
    val tryWithHandler =
      q"""
      try {
        doA()
      } catch(handler)
      """

    val tryWithHandlerTraversalResult = TestableTryWithHandlerTraversalResult(tryWithHandler, exprUncertainReturn = true)
    val expectedExprContext = BlockRenderContext(SimpleBlockStatRenderContext(uncertainReturn = true))
    val expectedTryContext = TryRenderContext(exprContext = expectedExprContext)

    doReturn(expectedTryContext).when(tryRenderContextFactory)(eqTryWithHandlerTraversalResult(tryWithHandlerTraversalResult))

    blockStatRenderContextFactory(tryWithHandlerTraversalResult) should equalBlockStatRenderContext(expectedTryContext)
  }
}
