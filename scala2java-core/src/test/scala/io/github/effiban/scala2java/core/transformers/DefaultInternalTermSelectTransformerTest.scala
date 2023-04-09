package io.github.effiban.scala2java.core.transformers

import io.github.effiban.scala2java.core.matchers.TermSelectTransformationContextMatcher.eqTermSelectTransformationContext
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.testtrees.TypeNames
import io.github.effiban.scala2java.spi.contexts.TermSelectTransformationContext
import io.github.effiban.scala2java.spi.transformers.TermSelectTransformer
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.XtensionQuasiquoteTerm

class DefaultInternalTermSelectTransformerTest extends UnitTestSuite {

  private val termSelectTransformer = mock[TermSelectTransformer]

  private val defaultInternalTermNameTransformer = new DefaultInternalTermSelectTransformer(termSelectTransformer)

  test("transform() when inner transformer returns a result should return it") {
    val termSelect = q"a.b"
    val context = TermSelectTransformationContext(Some(TypeNames.Int))
    val expectedTerm = q"x(123)"

    when(termSelectTransformer.transform(eqTree(termSelect), eqTermSelectTransformationContext(context))).thenReturn(Some(expectedTerm))

    defaultInternalTermNameTransformer.transform(termSelect, context).structure shouldBe expectedTerm.structure
  }

  test("transform() when inner transformer returns None should return input") {
    val termSelect = q"a.b"
    val context = TermSelectTransformationContext(Some(TypeNames.Int))

    when(termSelectTransformer.transform(eqTree(termSelect), eqTermSelectTransformationContext(context))).thenReturn(None)

    defaultInternalTermNameTransformer.transform(termSelect, context).structure shouldBe termSelect.structure
  }

}
