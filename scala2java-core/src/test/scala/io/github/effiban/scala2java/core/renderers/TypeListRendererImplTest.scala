package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.contexts.ArgumentListContext
import io.github.effiban.scala2java.core.entities.EnclosingDelimiter.AngleBracket
import io.github.effiban.scala2java.core.entities.ListTraversalOptions
import io.github.effiban.scala2java.core.matchers.ArgumentListContextMatcher.eqArgumentListContext
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.CombinedMatchers.eqTreeList
import org.mockito.ArgumentMatchersSugar.eqTo

import scala.meta.{Type, XtensionQuasiquoteType}

class TypeListRendererImplTest extends UnitTestSuite {

  private val ExpectedTraversalOptions = ListTraversalOptions(
    onSameLine = true,
    maybeEnclosingDelimiter = Some(AngleBracket)
  )

  private val argumentListRenderer = mock[ArgumentListRenderer]
  private val typeArgRenderer = mock[ArgumentRenderer[Type]]

  private val typeListRenderer = new TypeListRendererImpl(
    argumentListRenderer,
    typeArgRenderer
  )


  test("traverse() when no types") {
    typeListRenderer.render(Nil)

    verify(argumentListRenderer).render(
      args = eqTo(Nil),
      argRenderer = eqTo(typeArgRenderer),
      context = eqArgumentListContext(ArgumentListContext(options = ExpectedTraversalOptions))
    )
  }

  test("traverse() when one type") {
    val tpe = t"X"

    typeListRenderer.render(types = List(tpe))

    verify(argumentListRenderer).render(
      args = eqTreeList(List(tpe)),
      argRenderer = eqTo(typeArgRenderer),
      context = eqArgumentListContext(ArgumentListContext(options = ExpectedTraversalOptions))
    )
  }

  test("traverse() when two types") {
    val type1 = t"X"
    val type2 = t"Y"

    typeListRenderer.render(types = List(type1, type2))

    verify(argumentListRenderer).render(
      args = eqTreeList(List(type1, type2)),
      argRenderer = eqTo(typeArgRenderer),
      context = eqArgumentListContext(ArgumentListContext(options = ExpectedTraversalOptions))
    )
  }

}
