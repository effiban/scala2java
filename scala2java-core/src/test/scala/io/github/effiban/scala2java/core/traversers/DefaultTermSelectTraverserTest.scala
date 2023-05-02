package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.XtensionQuasiquoteTerm

class DefaultTermSelectTraverserTest extends UnitTestSuite {
  private val defaultTermRefTraverser = mock[DefaultTermRefTraverser]

  private val termSelectTraverser = new DefaultTermSelectTraverserImpl(defaultTermRefTraverser)

  test("traverse() when qualifier is a Term.Name") {
    val qualifier = q"myObj"
    val traversedQualifier = q"myTraversedObj"
    val termSelect = q"myObj.myMember"
    val traversedTermSelect = q"myTraversedObj.myMember"

    doAnswer(traversedQualifier).when(defaultTermRefTraverser).traverse(eqTree(qualifier))

    termSelectTraverser.traverse(termSelect).structure shouldBe traversedTermSelect.structure
  }

  test("traverse() when qualifier is a Term.Select") {
    val qualifier = q"myObj1.myObj2"
    val traversedQualifier = q"myTraversedObj1.myTraversedObj2"
    val termSelect = q"myObj1.myObj2.myMember"
    val traversedTermSelect = q"myTraversedObj1.myTraversedObj2.myMember"

    doAnswer(traversedQualifier).when(defaultTermRefTraverser).traverse(eqTree(qualifier))

    termSelectTraverser.traverse(termSelect).structure shouldBe traversedTermSelect.structure
  }
}
