package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.matchers.TreeMatcher.eqTree
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.transformers.TermTupleToTermApplyTransformer

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
