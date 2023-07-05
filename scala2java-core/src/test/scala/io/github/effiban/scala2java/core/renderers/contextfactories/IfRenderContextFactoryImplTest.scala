package io.github.effiban.scala2java.core.renderers.contextfactories

import io.github.effiban.scala2java.core.contexts.{BlockRenderContext, IfRenderContext, SimpleBlockStatRenderContext}
import io.github.effiban.scala2java.core.matchers.BlockTraversalResultMockitoMatcher
import io.github.effiban.scala2java.core.matchers.BlockTraversalResultMockitoMatcher.eqBlockTraversalResult
import io.github.effiban.scala2java.core.matchers.IfRenderContextScalatestMatcher.equalIfRenderContext
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.traversers.TestableBlockTraversalResult
import io.github.effiban.scala2java.core.traversers.results.{BlockTraversalResult, IfTraversalResult}
import org.mockito.ArgumentMatchers.any

import scala.meta.XtensionQuasiquoteTerm

class IfRenderContextFactoryImplTest extends UnitTestSuite {
  private val blockRenderContextFactory = mock[BlockRenderContextFactory]

  private val ifRenderContextFactory = new IfRenderContextFactoryImpl(blockRenderContextFactory)

  test("apply() when has only a 'then' clause") {
    val `if` =
      q"""
      if (x < 3) {
        doA1()
        doA2()
      }
      """

    val thenResult = TestableBlockTraversalResult(
      q"""
      {
        doA1()
        doA2()
      }
      """
    )

    val ifTraversalResult = IfTraversalResult(`if`.cond, thenResult)
    val expectedThenContext = BlockRenderContext(SimpleBlockStatRenderContext(uncertainReturn = true))
    val expectedIfContext = IfRenderContext(expectedThenContext)

    doReturn(expectedThenContext).when(blockRenderContextFactory)(eqBlockTraversalResult(thenResult))

    ifRenderContextFactory(ifTraversalResult) should equalIfRenderContext(expectedIfContext)
  }

  test("apply() when has both clauses") {
    val `if` =
      q"""
      if (x < 3) {
        doA1()
        doA2()
      } else {
        doB1()
        doB2()
      }
      """

    val thenResult = TestableBlockTraversalResult(
      q"""
      {
        doA1()
        doA2()
      }
      """
    )

    val elseResult = TestableBlockTraversalResult(
      q"""
      {
        doB1()
        doB2()
      }
      """
    )

    val ifTraversalResult = IfTraversalResult(`if`.cond, thenResult, Some(elseResult))
    val expectedThenContext = BlockRenderContext()
    val expectedElseContext = BlockRenderContext(SimpleBlockStatRenderContext(uncertainReturn = true))
    val expectedIfContext = IfRenderContext(expectedThenContext, expectedElseContext)

    doAnswer((result: BlockTraversalResult) => result match {
      case aResult if new BlockTraversalResultMockitoMatcher(thenResult).matches(aResult) => expectedThenContext
      case aResult if new BlockTraversalResultMockitoMatcher(elseResult).matches(aResult) => expectedElseContext
    }).when(blockRenderContextFactory)(any[BlockTraversalResult])

    ifRenderContextFactory(ifTraversalResult) should equalIfRenderContext(expectedIfContext)
  }
}
