package effiban.scala2java.traversers

import effiban.scala2java.contexts.TermContext
import effiban.scala2java.entities.EnclosingDelimiter.Parentheses
import effiban.scala2java.entities.JavaTreeType.Method
import effiban.scala2java.entities.ListTraversalOptions
import effiban.scala2java.matchers.CombinedMatchers.eqTreeList
import effiban.scala2java.matchers.TreeMatcher.eqTree
import effiban.scala2java.testsuites.UnitTestSuite
import org.mockito.{ArgumentCaptor, ArgumentMatchers}

import scala.meta.Term

class TermListTraverserImplTest extends UnitTestSuite {

  private val TheTermContext = TermContext(Method)

  private val argumentListTraverser = mock[ArgumentListTraverser]
  private val termTraverser = mock[TermTraverser]
  private val argTraverserCaptor: ArgumentCaptor[ScalaTreeTraverser[Term]] = ArgumentCaptor.forClass(classOf[ScalaTreeTraverser[Term]])

  private val termListTraverser = new TermListTraverserImpl(argumentListTraverser, termTraverser)


  test("traverse() when no terms") {
    traverse(terms = Nil)

    verifyArgumentListTraverserInvocation(Nil, ListTraversalOptions())
  }

  test("traverse() when one term and the rest default") {
    val term = Term.Name("x")

    traverse(terms = List(term))

    verifyArgumentListTraverserInvocation(List(term), ListTraversalOptions())
  }

  test("traverse() when one term, on same line and parentheses") {

    val term = Term.Name("x")
    val options = ListTraversalOptions(onSameLine = true, maybeEnclosingDelimiter = Some(Parentheses))

    traverse(terms = List(term), options = options)

    verifyArgumentListTraverserInvocation(List(term), options)
  }

  test("traverse() when two terms and the rest default") {
    val term1 = Term.Name("x")
    val term2 = Term.Name("y")
    val terms = List(term1, term2)

    traverse(terms = terms)

    verifyArgumentListTraverserInvocation(terms, ListTraversalOptions())
  }

  test("traverse() when two terms, on same line and parentheses") {
    val term1 = Term.Name("x")
    val term2 = Term.Name("y")
    val terms = List(term1, term2)

    val options = ListTraversalOptions(onSameLine = true, maybeEnclosingDelimiter = Some(Parentheses))

    traverse(terms = terms, options = options)

    verifyArgumentListTraverserInvocation(terms, options)
  }

  private def traverse(terms: List[Term], options: ListTraversalOptions = ListTraversalOptions()): Unit = {
    termListTraverser.traverse(
      terms = terms,
      options = options,
      context = TheTermContext)
  }

  private def verifyArgumentListTraverserInvocation(args: List[Term], options: ListTraversalOptions): Unit = {
    verify(argumentListTraverser).traverse(
      args = eqTreeList(args),
      argTraverser = argTraverserCaptor.capture(),
      options = ArgumentMatchers.eq(options)
    )

    val argTraverser = argTraverserCaptor.getValue
    val dummyTerm = Term.Name("dummy")
    argTraverser.traverse(dummyTerm)
    verify(termTraverser).traverse(eqTree(dummyTerm), ArgumentMatchers.eq(TheTermContext))
  }
}
