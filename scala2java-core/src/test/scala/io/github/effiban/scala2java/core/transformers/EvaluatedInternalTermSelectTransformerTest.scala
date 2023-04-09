package io.github.effiban.scala2java.core.transformers

import io.github.effiban.scala2java.core.matchers.TermSelectTransformationContextMatcher.eqTermSelectTransformationContext
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.testtrees.TypeNames
import io.github.effiban.scala2java.spi.contexts.TermSelectTransformationContext
import io.github.effiban.scala2java.spi.predicates.TermSelectSupportsNoArgInvocation
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.XtensionQuasiquoteTerm

class EvaluatedInternalTermSelectTransformerTest extends UnitTestSuite {

  private val defaultInternalTermSelectTransformer = mock[DefaultInternalTermSelectTransformer]
  private val termSelectSupportsNoArgInvocation = mock[TermSelectSupportsNoArgInvocation]

  private val internalTermSelectTransformer = new EvaluatedInternalTermSelectTransformer(
    defaultInternalTermSelectTransformer,
    termSelectSupportsNoArgInvocation
  )

  test("transform() when supports no-arg invocation - should return a corresponding Term.Apply with no args") {
    val termSelect = q"foo.bar"
    val context = TermSelectTransformationContext(Some(TypeNames.Int))
    val expectedTermApply = q"foo.bar()"

    when(termSelectSupportsNoArgInvocation(eqTree(termSelect))).thenReturn(true)

    internalTermSelectTransformer.transform(termSelect, context).structure shouldBe expectedTermApply.structure
  }

  test("transform() when does not support no-arg invocation - should call default transformer and return its result") {
    val termSelect = q"foo.bar"
    val context = TermSelectTransformationContext(Some(TypeNames.Int))
    val expectedTerm = q"javaFoo"

    when(termSelectSupportsNoArgInvocation(eqTree(termSelect))).thenReturn(false)
    when(defaultInternalTermSelectTransformer.transform(eqTree(termSelect), eqTermSelectTransformationContext(context)))
      .thenReturn(expectedTerm)

    internalTermSelectTransformer.transform(termSelect, context).structure shouldBe expectedTerm.structure
  }

}
