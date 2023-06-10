package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.contexts.ArgumentListContext
import io.github.effiban.scala2java.core.entities.ListTraversalOptions
import io.github.effiban.scala2java.core.matchers.ArgumentListContextMatcher.eqArgumentListContext
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.CombinedMatchers.eqTreeList
import org.mockito.ArgumentMatchersSugar.eqTo
import org.mockito.captor.ArgCaptor

import scala.meta.{Pat, XtensionQuasiquoteCaseOrPattern}

class PatListRendererImplTest extends UnitTestSuite {

  private val ExpectedArgListContext = ArgumentListContext(options = ListTraversalOptions(onSameLine = true))

  private val argumentListRenderer = mock[ArgumentListRenderer]
  private val patArgRenderer = mock[ArgumentRenderer[Pat]]

  private val argRendererProviderCaptor = ArgCaptor[Int => ArgumentRenderer[Pat]]

  private val patListRenderer = new PatListRendererImpl(argumentListRenderer, patArgRenderer)


  test("render() when no pats") {
    patListRenderer.render(Nil)

    verify(argumentListRenderer).render(
      args = eqTo(Nil),
      argRendererProvider = argRendererProviderCaptor.capture,
      context = eqArgumentListContext(ExpectedArgListContext)
    )

    argRendererProviderCaptor.value(0) shouldBe patArgRenderer
  }

  test("render() when one pat") {

    val pat = p"x"

    patListRenderer.render(List(pat))

    verify(argumentListRenderer).render(
      args = eqTreeList(List(pat)),
      argRendererProvider = argRendererProviderCaptor.capture,
      context = eqArgumentListContext(ExpectedArgListContext)
    )

    argRendererProviderCaptor.value(0) shouldBe patArgRenderer
  }

  test("render() when two pats") {
    val pat1 = p"x"
    val pat2 = p"y"

    patListRenderer.render(List(pat1, pat2))

    verify(argumentListRenderer).render(
      args = eqTreeList(List(pat1, pat2)),
      argRendererProvider = argRendererProviderCaptor.capture,
      context = eqArgumentListContext(ExpectedArgListContext)
    )

    argRendererProviderCaptor.value(0) shouldBe patArgRenderer
  }
}
