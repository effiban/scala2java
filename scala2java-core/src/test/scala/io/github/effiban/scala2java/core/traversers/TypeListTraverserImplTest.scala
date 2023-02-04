package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.ArgumentListContext
import io.github.effiban.scala2java.core.entities.EnclosingDelimiter._
import io.github.effiban.scala2java.core.entities.ListTraversalOptions
import io.github.effiban.scala2java.core.matchers.ArgumentListContextMatcher.eqArgumentListContext
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.CombinedMatchers.eqTreeList
import org.mockito.ArgumentMatchersSugar.eqTo

import scala.meta.Type

class TypeListTraverserImplTest extends UnitTestSuite {

  private val ExpectedTraversalOptions = ListTraversalOptions(
    onSameLine = true,
    maybeEnclosingDelimiter = Some(AngleBracket)
  )

  private val argumentListTraverser = mock[ArgumentListTraverser]
  private val typeArgTraverser = mock[ArgumentTraverser[Type]]

  private val typeListTraverser = new TypeListTraverserImpl(argumentListTraverser, typeArgTraverser)


  test("traverse() when no types") {
    typeListTraverser.traverse(Nil)

    verify(argumentListTraverser).traverse(
      args = eqTo(Nil),
      argTraverser = eqTo(typeArgTraverser),
      context = eqArgumentListContext(ArgumentListContext(options = ExpectedTraversalOptions))
    )
  }

  test("traverse() when one type") {

    val tpe = Type.Name("x")

    typeListTraverser.traverse(types = List(tpe))

    verify(argumentListTraverser).traverse(
      args = eqTreeList(List(tpe)),
      argTraverser = eqTo(typeArgTraverser),
      context = eqArgumentListContext(ArgumentListContext(options = ExpectedTraversalOptions))
    )
  }

  test("traverse() when two types") {
    val type1 = Type.Name("x")
    val type2 = Type.Name("y")

    typeListTraverser.traverse(types = List(type1, type2))

    verify(argumentListTraverser).traverse(
      args = eqTreeList(List(type1, type2)),
      argTraverser = eqTo(typeArgTraverser),
      context = eqArgumentListContext(ArgumentListContext(options = ExpectedTraversalOptions))
    )
  }
}
