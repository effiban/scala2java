package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.entities.EnclosingDelimiter._
import io.github.effiban.scala2java.core.entities.ListTraversalOptions
import io.github.effiban.scala2java.core.matchers.CombinedMatchers.eqTreeList
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.testtrees.TypeBounds
import org.mockito.ArgumentMatchers

import scala.meta.Type

class TypeParamListTraverserImplTest extends UnitTestSuite {

  private val ExpectedTraversalOptions = ListTraversalOptions(
    onSameLine = true,
    maybeEnclosingDelimiter = Some(AngleBracket)
  )

  private val argumentListTraverser = mock[ArgumentListTraverser]
  private val typeParamTraverser = mock[TypeParamTraverser]

  private val typeParamListTraverser = new TypeParamListTraverserImpl(argumentListTraverser, typeParamTraverser)


  test("traverse() when no params") {
    typeParamListTraverser.traverse(Nil)

    verify(argumentListTraverser).traverse(
      args = ArgumentMatchers.eq(Nil),
      argTraverser = ArgumentMatchers.eq(typeParamTraverser),
      options = ArgumentMatchers.eq(ExpectedTraversalOptions)
    )
  }

  test("traverse() when one param") {

    val param = typeParam("T")

    typeParamListTraverser.traverse(typeParams = List(param))

    verify(argumentListTraverser).traverse(
      args = eqTreeList(List(param)),
      argTraverser = ArgumentMatchers.eq(typeParamTraverser),
      options = ArgumentMatchers.eq(ExpectedTraversalOptions)
    )
  }

  test("traverse() when two params") {
    val param1 = typeParam("T1")
    val param2 = typeParam("T2")
    val params = List(param1, param2)

    typeParamListTraverser.traverse(typeParams = params)

    verify(argumentListTraverser).traverse(
      args = eqTreeList(params),
      argTraverser = ArgumentMatchers.eq(typeParamTraverser),
      options = ArgumentMatchers.eq(ExpectedTraversalOptions)
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