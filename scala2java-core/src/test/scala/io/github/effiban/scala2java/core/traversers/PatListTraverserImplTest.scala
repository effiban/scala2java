package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.entities.ListTraversalOptions
import io.github.effiban.scala2java.core.matchers.CombinedMatchers.eqTreeList
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import org.mockito.ArgumentMatchers

import scala.meta.{Pat, Term}

class PatListTraverserImplTest extends UnitTestSuite {

  private val ExpectedTraversalOptions = ListTraversalOptions(onSameLine = true)

  private val argumentListTraverser = mock[ArgumentListTraverser]
  private val patTraverser = mock[PatTraverser]

  private val patListTraverser = new PatListTraverserImpl(argumentListTraverser, patTraverser)


  test("traverse() when no pats") {
    patListTraverser.traverse(Nil)

    verify(argumentListTraverser).traverse(
      args = ArgumentMatchers.eq(Nil),
      argTraverser = ArgumentMatchers.eq(patTraverser),
      options = ArgumentMatchers.eq(ExpectedTraversalOptions)
    )
  }

  test("traverse() when one pat") {

    val pat = Pat.Var(Term.Name("x"))

    patListTraverser.traverse(List(pat))

    verify(argumentListTraverser).traverse(
      args = eqTreeList(List(pat)),
      argTraverser = ArgumentMatchers.eq(patTraverser),
      options = ArgumentMatchers.eq(ExpectedTraversalOptions)
    )
  }

  test("traverse() when two pats") {
    val pat1 = Pat.Var(Term.Name("x"))
    val pat2 = Pat.Var(Term.Name("y"))

    patListTraverser.traverse(List(pat1, pat2))

    verify(argumentListTraverser).traverse(
      args = eqTreeList(List(pat1, pat2)),
      argTraverser = ArgumentMatchers.eq(patTraverser),
      options = ArgumentMatchers.eq(ExpectedTraversalOptions)
    )
  }
}
