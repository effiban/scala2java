package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.contexts.ArgumentListContext
import io.github.effiban.scala2java.core.entities.ListTraversalOptions
import io.github.effiban.scala2java.core.matchers.ArgumentListContextMatcher.eqArgumentListContext
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.CombinedMatchers.eqTreeList
import org.mockito.ArgumentMatchersSugar.eqTo

import scala.meta.{Pat, XtensionQuasiquoteCaseOrPattern}

class PatListRendererImplTest extends UnitTestSuite {

  private val ExpectedArgListContext = ArgumentListContext(options = ListTraversalOptions(onSameLine = true))

  private val argumentListRenderer = mock[ArgumentListRenderer]
  private val patArgRenderer = mock[ArgumentRenderer[Pat]]

  private val patListRenderer = new PatListRendererImpl(argumentListRenderer, patArgRenderer)


  test("render() when no pats") {
    patListRenderer.render(Nil)

    verify(argumentListRenderer).render(
      args = eqTo(Nil),
      argRenderer = eqTo(patArgRenderer),
      context = eqArgumentListContext(ExpectedArgListContext)
    )
  }

  test("render() when one pat") {

    val pat = p"x"

    patListRenderer.render(List(pat))

    verify(argumentListRenderer).render(
      args = eqTreeList(List(pat)),
      argRenderer = eqTo(patArgRenderer),
      context = eqArgumentListContext(ExpectedArgListContext)
    )
  }

  test("render() when two pats") {
    val pat1 = p"x"
    val pat2 = p"y"

    patListRenderer.render(List(pat1, pat2))

    verify(argumentListRenderer).render(
      args = eqTreeList(List(pat1, pat2)),
      argRenderer = eqTo(patArgRenderer),
      context = eqArgumentListContext(ExpectedArgListContext)
    )
  }
}
