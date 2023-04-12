package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.XtensionQuasiquoteTerm

class EvaluatedStandardApplyTypeTraverserTest extends UnitTestSuite {

  private val termApplyTraverser = mock[TermApplyTraverser]
  private val evaluatedStandardApplyTypeTraverser = new EvaluatedStandardApplyTypeTraverser(termApplyTraverser)

  test("traverse when 'fun' is a Term.Name") {
    val termApplyType = q"foo[Int]"
    val expectedTermApply = q"foo[Int]()"

    evaluatedStandardApplyTypeTraverser.traverse(termApplyType)

    verify(termApplyTraverser).traverse(eqTree(expectedTermApply))
  }

  test("traverse when 'fun' is a Term.Select") {
    val termApplyType = q"foo.bar[Int]"
    val expectedTermApply = q"foo.bar[Int]()"

    evaluatedStandardApplyTypeTraverser.traverse(termApplyType)

    verify(termApplyTraverser).traverse(eqTree(expectedTermApply))
  }
}
