package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.contexts.{ArgumentListContext, InitContext}
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.CombinedMatchers.eqTreeList
import org.mockito.ArgumentMatchersSugar.eqTo

import scala.meta.XtensionQuasiquoteInit

class InitListRendererImplTest extends UnitTestSuite {

  private val argumentListRenderer = mock[ArgumentListRenderer]
  private val initArgRendererFactory = mock[InitArgRendererFactory]
  private val initArgRenderer = mock[InitArgumentRenderer]

  private val initListRenderer = new InitListRendererImpl(argumentListRenderer, initArgRendererFactory)


  test("render() when no inits") {
    when(initArgRendererFactory(InitContext())).thenReturn(initArgRenderer)

    initListRenderer.render(Nil)

    verify(argumentListRenderer).render(args = Nil, argRenderer = initArgRenderer, context = ArgumentListContext())
  }

  test("render() when two inits") {
    val init1 = init"MyType1(arg1, arg2)"
    val init2 = init"MyType2(arg3, arg4)"

    when(initArgRendererFactory(InitContext())).thenReturn(initArgRenderer)

    initListRenderer.render(List(init1, init2))

    verify(argumentListRenderer).render(
      args = eqTreeList(List(init1, init2)),
      argRenderer = eqTo(initArgRenderer),
      context = eqTo(ArgumentListContext())
    )
  }
}
