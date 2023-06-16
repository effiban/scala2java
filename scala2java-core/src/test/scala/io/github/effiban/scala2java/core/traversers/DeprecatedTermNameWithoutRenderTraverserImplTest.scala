package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.spi.transformers.TermNameTransformer
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.XtensionQuasiquoteTerm

class DeprecatedTermNameWithoutRenderTraverserImplTest extends UnitTestSuite {

  private val expressionTermTraverser = mock[DeprecatedExpressionTermTraverser]
  private val termNameTransformer = mock[TermNameTransformer]

  private val termNameWithoutRenderTraverser = new DeprecatedTermNameWithoutRenderTraverserImpl(
    expressionTermTraverser,
    termNameTransformer,
  )

  test("traverse when transformer returns a Term.Name") {
    val inputTermName = q"in"
    val outputTermName = q"out"

    when(termNameTransformer.transform(eqTree(inputTermName))).thenReturn(Some(outputTermName))

    termNameWithoutRenderTraverser.traverse(inputTermName).value.structure shouldBe outputTermName.structure
  }

  test("traverse when transformer returns a Term.Select") {
    val termName = q"in"
    val termSelect = q"a.in"

    when(termNameTransformer.transform(eqTree(termName))).thenReturn(Some(termSelect))

    termNameWithoutRenderTraverser.traverse(termName) shouldBe None

    verify(expressionTermTraverser).traverse(eqTree(termSelect))
  }

  test("traverse when transformer returns None should return the input Term.Name") {
    val termName = q"in"

    when(termNameTransformer.transform(eqTree(termName))).thenReturn(None)

    termNameWithoutRenderTraverser.traverse(termName).value.structure shouldBe termName.structure
  }
}
