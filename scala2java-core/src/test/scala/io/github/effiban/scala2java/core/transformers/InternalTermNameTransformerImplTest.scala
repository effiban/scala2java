package io.github.effiban.scala2java.core.transformers

import io.github.effiban.scala2java.core.contexts.InternalTermNameTransformationContext
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.spi.transformers.TermNameTransformer
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.XtensionQuasiquoteTerm

class InternalTermNameTransformerImplTest extends UnitTestSuite {

  private val termNameTransformer = mock[TermNameTransformer]
  private val internalTermNameTransformer = new InternalTermNameTransformerImpl(termNameTransformer)

  test("transform() when inner transformer returns a term should return it") {
    val termName = q"foo"
    val context = InternalTermNameTransformationContext(Some(q"fooParent"))
    val expectedTerm = q"javaFoo"

    when(termNameTransformer.transform(eqTree(termName))).thenReturn(Some(expectedTerm))

    internalTermNameTransformer.transform(termName, context).structure shouldBe expectedTerm.structure
  }

  test("transform() when inner transformer returns None should return the input unchanged") {
    val termName = q"foo"
    val context = InternalTermNameTransformationContext(Some(q"fooParent"))

    when(termNameTransformer.transform(eqTree(termName))).thenReturn(None)

    internalTermNameTransformer.transform(termName, context).structure shouldBe termName.structure
  }
}
