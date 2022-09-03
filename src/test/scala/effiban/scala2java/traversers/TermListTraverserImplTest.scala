package effiban.scala2java.traversers

import effiban.scala2java.entities.EnclosingDelimiter.Parentheses
import effiban.scala2java.entities.ListTraversalOptions
import effiban.scala2java.matchers.CombinedMatchers.eqTreeList
import effiban.scala2java.testsuites.UnitTestSuite
import org.mockito.ArgumentMatchers

import scala.meta.Term

class TermListTraverserImplTest extends UnitTestSuite {

  private val argumentListTraverser = mock[ArgumentListTraverser]
  private val termTraverser = mock[TermTraverser]

  private val termListTraverser = new TermListTraverserImpl(argumentListTraverser, termTraverser)


  test("traverse() when no terms") {
    termListTraverser.traverse(Nil)

    verify(argumentListTraverser).traverse(
      args = ArgumentMatchers.eq(Nil),
      argTraverser = ArgumentMatchers.eq(termTraverser),
      options = ArgumentMatchers.eq(ListTraversalOptions())
    )
  }

  test("traverse() when one term and the rest default") {

    val term = Term.Name("x")

    termListTraverser.traverse(terms = List(term))

    verify(argumentListTraverser).traverse(
      args = eqTreeList(List(term)),
      argTraverser = ArgumentMatchers.eq(termTraverser),
      options = ArgumentMatchers.eq(ListTraversalOptions())
    )
  }

  test("traverse() when one term, on same line and parentheses") {

    val term = Term.Name("x")

    val options = ListTraversalOptions(onSameLine = true, maybeEnclosingDelimiter = Some(Parentheses))

    termListTraverser.traverse(terms = List(term), options = options)

    verify(argumentListTraverser).traverse(
      args = eqTreeList(List(term)),
      argTraverser = ArgumentMatchers.eq(termTraverser),
      options = ArgumentMatchers.eq(options)
    )
  }

  test("traverse() when two terms and the rest default") {
    val term1 = Term.Name("x")
    val term2 = Term.Name("y")

    termListTraverser.traverse(terms = List(term1, term2))

    verify(argumentListTraverser).traverse(
      args = eqTreeList(List(term1, term2)),
      argTraverser = ArgumentMatchers.eq(termTraverser),
      options = ArgumentMatchers.eq(ListTraversalOptions())
    )
  }

  test("traverse() when two terms, on same line and parentheses") {
    val term1 = Term.Name("x")
    val term2 = Term.Name("y")

    val options = ListTraversalOptions(onSameLine = true, maybeEnclosingDelimiter = Some(Parentheses))

    termListTraverser.traverse(terms = List(term1, term2), options = options)

    verify(argumentListTraverser).traverse(
      args = eqTreeList(List(term1, term2)),
      argTraverser = ArgumentMatchers.eq(termTraverser),
      options = ArgumentMatchers.eq(options)
    )
  }
}
