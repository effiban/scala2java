package effiban.scala2java.traversers

import effiban.scala2java.entities.EnclosingDelimiter.Parentheses
import effiban.scala2java.matchers.TreeListMatcher.eqTreeList
import effiban.scala2java.testsuites.UnitTestSuite
import org.mockito.ArgumentMatchers

import scala.meta.Term
import scala.meta.Term.Tuple

class TermTupleTraverserImplTest extends UnitTestSuite {

  private val termListTraverser = mock[TermListTraverser]

  private val termTupleTraverser = new TermTupleTraverserImpl(termListTraverser)

  test("traverse") {
    val terms = List(Term.Name("x"), Term.Name("y"))
    val tuple = Tuple(terms)

    termTupleTraverser.traverse(tuple)

    verify(termListTraverser).traverse(
      terms = eqTreeList(terms),
      onSameLine = ArgumentMatchers.eq(true),
      maybeEnclosingDelimiter = ArgumentMatchers.eq(Some(Parentheses))
    )
  }
}
