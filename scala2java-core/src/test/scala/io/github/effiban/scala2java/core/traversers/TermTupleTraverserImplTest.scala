package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.transformers.TermTupleToTermApplyTransformer
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.Term.Tuple
import scala.meta.XtensionQuasiquoteTerm

class TermTupleTraverserImplTest extends UnitTestSuite {

  private val termApplyTraverser = mock[TermApplyTraverser]
  private val termTupleToTermApplyTransformer = mock[TermTupleToTermApplyTransformer]

  private val termTupleTraverser = new TermTupleTraverserImpl(termApplyTraverser, termTupleToTermApplyTransformer)

  test("traverse") {
    val terms = List(q"1", q"2")
    val tuple = Tuple(terms)
    val expectedIntermediateTermApply = q"Tuple.tuple(1, 2)"
    val expectedTraversedTermApply = q"Tuple.tuple(11, 22)"

    when(termTupleToTermApplyTransformer.transform(eqTree(tuple))).thenReturn(expectedIntermediateTermApply)
    doReturn(expectedTraversedTermApply).when(termApplyTraverser).traverse(eqTree(expectedIntermediateTermApply))

    termTupleTraverser.traverse(tuple).structure shouldBe expectedTraversedTermApply.structure
  }
}
