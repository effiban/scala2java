package effiban.scala2java

import effiban.scala2java.matchers.TreeListMatcher.eqTreeList
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
      maybeDelimiterType = ArgumentMatchers.eq(Some(Parentheses))
    )
  }
}
