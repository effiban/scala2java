package io.github.effiban.scala2java.core.renderers.contextfactories

import io.github.effiban.scala2java.core.contexts.{BlockRenderContext, SimpleBlockStatRenderContext}
import io.github.effiban.scala2java.core.matchers.BlockRenderContextScalatestMatcher.equalBlockRenderContext
import io.github.effiban.scala2java.core.matchers.BlockStatTraversalResultMockitoMatcher.eqBlockStatTraversalResult
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.traversers.results.{BlockTraversalResult, SimpleBlockStatTraversalResult}

import scala.meta.{XtensionQuasiquoteInit, XtensionQuasiquoteTerm}

class BlockRenderContextFactoryImplTest extends UnitTestSuite {

  private val blockStatRenderContextFactory = mock[BlockStatRenderContextFactory]

  private val blockRenderContextFactory = new BlockRenderContextFactoryImpl(blockStatRenderContextFactory)

  test("apply() when result has non-last stats, last stat result and init") {
    val init = init"MyClass(2)"

    val lastStatTraversalResult = SimpleBlockStatTraversalResult(q"stat2", uncertainReturn = true)
    val blockTraversalResult = BlockTraversalResult(
      nonLastStats = List(q"stat1"),
      maybeLastStatResult = Some(lastStatTraversalResult),
    )

    val expectedBlockStatContext = SimpleBlockStatRenderContext(uncertainReturn = true)
    val expectedBlockContext = BlockRenderContext(lastStatContext = expectedBlockStatContext)

    doReturn(expectedBlockStatContext).when(blockStatRenderContextFactory)(eqBlockStatTraversalResult(lastStatTraversalResult))

    blockRenderContextFactory(blockTraversalResult) should equalBlockRenderContext(expectedBlockContext)
  }

  test("apply() when result is empty") {
    blockRenderContextFactory(BlockTraversalResult()) should equalBlockRenderContext(BlockRenderContext())
  }
}
