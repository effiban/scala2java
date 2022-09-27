package effiban.scala2java.traversers

import effiban.scala2java.matchers.TreeMatcher.eqTree
import effiban.scala2java.testsuites.UnitTestSuite
import effiban.scala2java.transformers.TermTupleToTermApplyTransformer

import scala.meta.Term.Tuple
import scala.meta.{Lit, Term}

class TermTupleTraverserImplTest extends UnitTestSuite {

  private val termApplyTraverser = mock[TermApplyTraverser]
  private val termTupleToTermApplyTransformer = mock[TermTupleToTermApplyTransformer]

  private val termTupleTraverser = new TermTupleTraverserImpl(termApplyTraverser, termTupleToTermApplyTransformer)

  test("traverse") {
    val terms = List(Lit.Int(1), Lit.Int(2))
    val tuple = Tuple(terms)
    val expectedTermApply = Term.Apply(Term.Select(Term.Name("Tuple"), Term.Name("tuple")), terms)

    when(termTupleToTermApplyTransformer.transform(eqTree(tuple))).thenReturn(expectedTermApply)

    termTupleTraverser.traverse(tuple)

    verify(termApplyTraverser).traverse(eqTree(expectedTermApply))
  }
}
