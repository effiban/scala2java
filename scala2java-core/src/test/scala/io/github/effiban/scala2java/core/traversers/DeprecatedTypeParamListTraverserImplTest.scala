package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.ArgumentListContext
import io.github.effiban.scala2java.core.entities.EnclosingDelimiter._
import io.github.effiban.scala2java.core.entities.ListTraversalOptions
import io.github.effiban.scala2java.core.matchers.ArgumentListContextMatcher.eqArgumentListContext
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.testtrees.TypeBounds
import io.github.effiban.scala2java.test.utils.matchers.CombinedMatchers.eqTreeList
import org.mockito.ArgumentMatchersSugar.eqTo

import scala.meta.Type

class DeprecatedTypeParamListTraverserImplTest extends UnitTestSuite {

  private val ExpectedTraversalOptions = ListTraversalOptions(
    onSameLine = true,
    maybeEnclosingDelimiter = Some(AngleBracket)
  )

  private val argumentListTraverser = mock[DeprecatedArgumentListTraverser]
  private val typeParamArgTraverser = mock[DeprecatedArgumentTraverser[Type.Param]]

  private val typeParamListTraverser = new DeprecatedTypeParamListTraverserImpl(argumentListTraverser, typeParamArgTraverser)


  test("traverse() when no params") {
    typeParamListTraverser.traverse(Nil)

    verify(argumentListTraverser).traverse(
      args = eqTo(Nil),
      argTraverser = eqTo(typeParamArgTraverser),
      context = eqArgumentListContext(ArgumentListContext(options = ExpectedTraversalOptions))
    )
  }

  test("traverse() when two params") {
    val param1 = typeParam("T1")
    val param2 = typeParam("T2")
    val params = List(param1, param2)

    typeParamListTraverser.traverse(typeParams = params)

    verify(argumentListTraverser).traverse(
      args = eqTreeList(params),
      argTraverser = eqTo(typeParamArgTraverser),
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
