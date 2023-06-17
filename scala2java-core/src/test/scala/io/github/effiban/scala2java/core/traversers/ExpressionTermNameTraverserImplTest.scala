package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.spi.transformers.TermNameTransformer
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.XtensionQuasiquoteTerm

class ExpressionTermNameTraverserImplTest extends UnitTestSuite {

  private val expressionTermTraverser = mock[ExpressionTermTraverser]
  private val termNameTransformer = mock[TermNameTransformer]

  private val expressionTermNameTraverser = new ExpressionTermNameTraverserImpl(
    expressionTermTraverser,
    termNameTransformer
  )

  test("traverse when transformer returns a Term.Name") {
    val inputTermName = q"in"
    val outputTermName = q"out"

    when(termNameTransformer.transform(eqTree(inputTermName))).thenReturn(Some(outputTermName))

    expressionTermNameTraverser.traverse(inputTermName).structure shouldBe outputTermName.structure
  }

  test("traverse when transformer returns a Term.Select") {
    val termName = q"in"
    val termSelect = q"a.in"
    val traversedTermSelect = q"b.in"

    when(termNameTransformer.transform(eqTree(termName))).thenReturn(Some(termSelect))
    doReturn(traversedTermSelect).when(expressionTermTraverser).traverse(eqTree(termSelect))

    expressionTermNameTraverser.traverse(termName).structure shouldBe traversedTermSelect.structure
  }

  test("traverse when transformer returns None should return the input Term.Name") {
    val termName = q"in"

    when(termNameTransformer.transform(eqTree(termName))).thenReturn(None)

    expressionTermNameTraverser.traverse(termName).structure shouldBe termName.structure
  }
}
