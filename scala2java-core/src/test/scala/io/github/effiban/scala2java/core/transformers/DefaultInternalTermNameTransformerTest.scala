package io.github.effiban.scala2java.core.transformers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.spi.transformers.TermNameTransformer
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.XtensionQuasiquoteTerm

class DefaultInternalTermNameTransformerTest extends UnitTestSuite {

  private val termNameTransformer = mock[TermNameTransformer]

  private val defaultInternalTermNameTransformer = new DefaultInternalTermNameTransformer(termNameTransformer)

  test("transform() when inner transformer returns a result should return it") {
    val termName = q"abc"
    val expectedTerm = q"x.y"

    when(termNameTransformer.transform(eqTree(termName))).thenReturn(Some(expectedTerm))

    defaultInternalTermNameTransformer.transform(termName).structure shouldBe expectedTerm.structure
  }

  test("transform() when inner transformer returns None should return input") {
    val termName = q"abc"

    when(termNameTransformer.transform(eqTree(termName))).thenReturn(None)

    defaultInternalTermNameTransformer.transform(termName).structure shouldBe termName.structure
  }
}
