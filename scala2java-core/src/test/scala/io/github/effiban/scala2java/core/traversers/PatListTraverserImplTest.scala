package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.ArgumentListContext
import io.github.effiban.scala2java.core.entities.ListTraversalOptions
import io.github.effiban.scala2java.core.matchers.ArgumentListContextMatcher.eqArgumentListContext
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.CombinedMatchers.eqTreeList
import org.mockito.ArgumentMatchersSugar.eqTo

import scala.meta.{Pat, Term}

class PatListTraverserImplTest extends UnitTestSuite {

  private val ExpectedArgListContext = ArgumentListContext(options = ListTraversalOptions(onSameLine = true))

  private val argumentListTraverser = mock[ArgumentListTraverser]
  private val patArgTraverser = mock[ArgumentTraverser[Pat]]

  private val patListTraverser = new PatListTraverserImpl(argumentListTraverser, patArgTraverser)


  test("traverse() when no pats") {
    patListTraverser.traverse(Nil)

    verify(argumentListTraverser).traverse(
      args = eqTo(Nil),
      argTraverser = eqTo(patArgTraverser),
      context = eqArgumentListContext(ExpectedArgListContext)
    )
  }

  test("traverse() when one pat") {

    val pat = Pat.Var(Term.Name("x"))

    patListTraverser.traverse(List(pat))

    verify(argumentListTraverser).traverse(
      args = eqTreeList(List(pat)),
      argTraverser = eqTo(patArgTraverser),
      context = eqArgumentListContext(ExpectedArgListContext)
    )
  }

  test("traverse() when two pats") {
    val pat1 = Pat.Var(Term.Name("x"))
    val pat2 = Pat.Var(Term.Name("y"))

    patListTraverser.traverse(List(pat1, pat2))

    verify(argumentListTraverser).traverse(
      args = eqTreeList(List(pat1, pat2)),
      argTraverser = eqTo(patArgTraverser),
      context = eqArgumentListContext(ExpectedArgListContext)
    )
  }
}
