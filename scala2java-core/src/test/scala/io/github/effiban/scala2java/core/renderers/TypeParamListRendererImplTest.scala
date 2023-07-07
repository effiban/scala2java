package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.contexts.ArgumentListContext
import io.github.effiban.scala2java.core.entities.EnclosingDelimiter._
import io.github.effiban.scala2java.core.entities.ListTraversalOptions
import io.github.effiban.scala2java.core.matchers.ArgumentListContextMatcher.eqArgumentListContext
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.testtrees.TypeBounds
import io.github.effiban.scala2java.test.utils.matchers.CombinedMatchers.eqTreeList
import org.mockito.ArgumentMatchersSugar.eqTo

import scala.meta.Type

class TypeParamListRendererImplTest extends UnitTestSuite {

  private val ExpectedTraversalOptions = ListTraversalOptions(
    onSameLine = true,
    maybeEnclosingDelimiter = Some(AngleBracket)
  )

  private val argumentListRenderer = mock[ArgumentListRenderer]
  private val typeParamArgRenderer = mock[ArgumentRenderer[Type.Param]]

  private val typeParamListRenderer = new TypeParamListRendererImpl(argumentListRenderer, typeParamArgRenderer)


  test("render() when no params") {
    typeParamListRenderer.render(Nil)

    verify(argumentListRenderer).render(
      args = eqTo(Nil),
      argRenderer = eqTo(typeParamArgRenderer),
      context = eqArgumentListContext(ArgumentListContext(options = ExpectedTraversalOptions))
    )
  }

  test("render() when two params") {
    val param1 = typeParam("T1")
    val param2 = typeParam("T2")
    val params = List(param1, param2)

    typeParamListRenderer.render(typeParams = params)

    verify(argumentListRenderer).render(
      args = eqTreeList(params),
      argRenderer = eqTo(typeParamArgRenderer),
      context = eqArgumentListContext(ArgumentListContext(options = ExpectedTraversalOptions))
    )
  }

  private def typeParam(name: String) = {
    Type.Param(
      mods = Nil,
      name = Type.Name(name),
      tparams = Nil,
      tbounds = TypeBounds.Empty,
      vbounds = Nil,
      cbounds = Nil
    )
  }
}
