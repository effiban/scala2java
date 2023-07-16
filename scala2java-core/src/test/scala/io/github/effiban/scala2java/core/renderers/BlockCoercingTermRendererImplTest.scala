package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.renderers.contexts.BlockRenderContext
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchersSugar.eqTo

import scala.meta.XtensionQuasiquoteTerm

class BlockCoercingTermRendererImplTest extends UnitTestSuite {

  private val blockRenderer = mock[BlockRenderer]

  private val blockCoercingTermRenderer = new BlockCoercingTermRendererImpl(blockRenderer)

  test("render() when term is a Block") {
    val block =
      q"""
      {
         doSomething()
         doSomethingElse()
      }
      """

    val context = BlockRenderContext(uncertainReturn = true)

    blockCoercingTermRenderer.render(block, context)

    verify(blockRenderer).render(eqTree(block), eqTo(context))
  }

  test("render() when term is not a Block should throw an IllegalStateException") {
    val term = q"x"

    val context = BlockRenderContext(uncertainReturn = true)

    intercept[IllegalStateException] {
      blockCoercingTermRenderer.render(term, context)
    }
  }
}
