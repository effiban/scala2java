package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.contexts.{BlockRenderContext, IfRenderContext}
import io.github.effiban.scala2java.core.matchers.BlockRenderContextMatcher.eqBlockRenderContext
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchersSugar.eqTo

import scala.meta.XtensionQuasiquoteTerm

class DefaultTermRendererImplTest extends UnitTestSuite {

  private val defaultTermRefRenderer = mock[DefaultTermRefRenderer]
  private val termApplyRenderer = mock[TermApplyRenderer]
  private val applyTypeRenderer = mock[ApplyTypeRenderer]
  private val applyInfixRenderer = mock[TermApplyInfixRenderer]
  private val assignRenderer = mock[AssignRenderer]
  private val returnRenderer = mock[ReturnRenderer]
  private val blockRenderer = mock[BlockRenderer]
  private val ifRenderer = mock[IfRenderer]
  private val litRenderer = mock[LitRenderer]

  private val defaultTermRenderer = new DefaultTermRendererImpl(
    defaultTermRefRenderer,
    termApplyRenderer,
    applyTypeRenderer,
    applyInfixRenderer,
    assignRenderer,
    returnRenderer,
    blockRenderer,
    ifRenderer,
    litRenderer
  )

  test("render Term.Name") {
    val termName = q"x"

    defaultTermRenderer.render(termName)

    verify(defaultTermRefRenderer).render(eqTree(termName))
  }

  test("render Term.Apply") {
    val termApply = q"a(x, y)"

    defaultTermRenderer.render(termApply)

    verify(termApplyRenderer).render(eqTree(termApply))
  }

  test("render Term.ApplyType") {
    val applyType = q"a[T]"

    defaultTermRenderer.render(applyType)

    verify(applyTypeRenderer).render(eqTree(applyType))
  }

  test("render Term.ApplyInfix") {
    val applyInfix = q"a + b"

    defaultTermRenderer.render(applyInfix)

    verify(applyInfixRenderer).render(eqTree(applyInfix))
  }

  test("render Term.Assign") {
    val assign = q"a = 3"

    defaultTermRenderer.render(assign)

    verify(assignRenderer).render(eqTree(assign))
  }

  test("render Term.Return") {
    val `return` = q"return x"
    defaultTermRenderer.render(`return`)
    verify(returnRenderer).render(eqTree(`return`))
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

  test("render If") {
    val termIf =
      q"""
      if (cond) {
        doSomething()
      } else {
        doSomethingElse()
      }
      """

    defaultTermRenderer.render(termIf)

    verify(ifRenderer).render(eqTree(termIf), eqTo(IfRenderContext()))
  }

  test("render Lit") {
    val lit = q"3"

    defaultTermRenderer.render(lit)

    verify(litRenderer).render(eqTree(lit))
  }
}
