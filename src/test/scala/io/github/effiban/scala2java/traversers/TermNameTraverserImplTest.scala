package io.github.effiban.scala2java.traversers

import io.github.effiban.scala2java.matchers.TreeMatcher.eqTree
import io.github.effiban.scala2java.testsuites.UnitTestSuite
import io.github.effiban.scala2java.testtrees.TermNames
import io.github.effiban.scala2java.testtrees.TermNames.{Empty, ScalaOption}
import io.github.effiban.scala2java.transformers.TermNameTransformer

import scala.meta.Term

class TermNameTraverserImplTest extends UnitTestSuite {

  private val termTraverser = mock[TermTraverser]
  private val termNameTransformer = mock[TermNameTransformer]

  private val termNameTraverser = new TermNameTraverserImpl(termTraverser, termNameTransformer)

  test("traverse when transformer returns the same") {
    val termName = Term.Name("xyz")

    when(termNameTransformer.transform(eqTree(termName))).thenReturn(termName)

    termNameTraverser.traverse(termName)

    outputWriter.toString shouldBe "xyz"
  }

  test("traverse when transformer returns different term") {
    val termName = TermNames.ScalaNone
    val term = Term.Select(ScalaOption, Empty)

    when(termNameTransformer.transform(eqTree(termName))).thenReturn(term)

    termNameTraverser.traverse(termName)

    verify(termTraverser).traverse(eqTree(term))
  }
}
