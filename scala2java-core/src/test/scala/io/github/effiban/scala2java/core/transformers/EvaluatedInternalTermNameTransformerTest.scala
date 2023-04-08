package io.github.effiban.scala2java.core.transformers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.spi.predicates.TermNameSupportsNoArgInvocation
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.XtensionQuasiquoteTerm

class EvaluatedInternalTermNameTransformerTest extends UnitTestSuite {

  private val defaultInternalTermNameTransformer = mock[DefaultInternalTermNameTransformer]
  private val termNameSupportsNoArgInvocation = mock[TermNameSupportsNoArgInvocation]

  private val internalTermNameTransformer = new EvaluatedInternalTermNameTransformer(
    defaultInternalTermNameTransformer,
    termNameSupportsNoArgInvocation
  )

  test("transform() when supports no-arg invocation - should return a Term.Apply") {
    val termName = q"foo"
    val expectedTermApply = q"foo()"

    when(termNameSupportsNoArgInvocation(eqTree(termName))).thenReturn(true)

    internalTermNameTransformer.transform(termName).structure shouldBe expectedTermApply.structure
  }

  test("transform() when does not support no-arg invocation - should call default transformer and return its result") {
    val termName = q"foo"
    val expectedTerm = q"javaFoo"

    when(termNameSupportsNoArgInvocation(eqTree(termName))).thenReturn(false)
    when(defaultInternalTermNameTransformer.transform(eqTree(termName))).thenReturn(expectedTerm)

    internalTermNameTransformer.transform(termName).structure shouldBe expectedTerm.structure
  }

}
