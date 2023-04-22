package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.transformers.InternalTermNameTransformer
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.XtensionQuasiquoteTerm

class TermNameWithoutRenderTraverserImplTest extends UnitTestSuite {

  private val termTraverser = mock[TermTraverser]
  private val termNameTransformer = mock[InternalTermNameTransformer]

  private val termNameWithoutRenderTraverser = new TermNameWithoutRenderTraverserImpl(
    termTraverser,
    termNameTransformer,
  )

  test("traverse when transformer returns a Term.Name") {
    val inputTermName = q"in"
    val outputTermName = q"out"

    when(termNameTransformer.transform(eqTree(inputTermName))).thenReturn(outputTermName)

    termNameWithoutRenderTraverser.traverse(inputTermName).value.structure shouldBe outputTermName.structure
  }

  test("traverse when transformer returns a Term.Select") {
    val termName = q"in"
    val termSelect = q"a.in"

    when(termNameTransformer.transform(eqTree(termName))).thenReturn(termSelect)

    termNameWithoutRenderTraverser.traverse(termName) shouldBe None

    verify(termTraverser).traverse(eqTree(termSelect))
  }
}
