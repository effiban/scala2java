package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.contexts.BlockRenderContext
import io.github.effiban.scala2java.core.matchers.BlockRenderContextMatcher.eqBlockRenderContext
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.XtensionQuasiquoteTerm

class DefaultTermRendererImplTest extends UnitTestSuite {

  private val defaultTermRefRenderer = mock[DefaultTermRefRenderer]
  private val applyTypeRenderer = mock[ApplyTypeRenderer]
  private val blockRenderer = mock[BlockRenderer]
  private val litRenderer = mock[LitRenderer]

  private val defaultTermRenderer = new DefaultTermRendererImpl(
    defaultTermRefRenderer,
    applyTypeRenderer,
    blockRenderer,
    litRenderer
  )

  test("render Term.Name") {
    val termName = q"x"

    defaultTermRenderer.render(termName)

    verify(defaultTermRefRenderer).render(eqTree(termName))
  }

  test("render Term.ApplyType") {
    val applyType = q"a[T]"

    defaultTermRenderer.render(applyType)

    verify(applyTypeRenderer).render(eqTree(applyType))
  }

  test("render Block") {
    val block =
      q"""
      {
        x = calcX()
        y = calcY()
        x + y
      }
      """

    defaultTermRenderer.render(block)

    verify(blockRenderer).render(eqTree(block), eqBlockRenderContext(BlockRenderContext()))
  }

  test("render Lit") {
    val lit = q"3"

    defaultTermRenderer.render(lit)

    verify(litRenderer).render(eqTree(lit))
  }
}
