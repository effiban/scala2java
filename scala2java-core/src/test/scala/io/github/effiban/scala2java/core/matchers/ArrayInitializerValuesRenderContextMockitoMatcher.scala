package io.github.effiban.scala2java.core.matchers

import io.github.effiban.scala2java.core.contexts.ArrayInitializerValuesRenderContext
import io.github.effiban.scala2java.test.utils.matchers.{ListMatcher, TreeMatcher}
import org.mockito.ArgumentMatcher
import org.mockito.ArgumentMatchers.argThat

import scala.meta.{Term, Type}

class ArrayInitializerValuesRenderContextMockitoMatcher(expectedContext: ArrayInitializerValuesRenderContext) 
  extends ArgumentMatcher[ArrayInitializerValuesRenderContext] {

  override def matches(actualContext: ArrayInitializerValuesRenderContext): Boolean = {
      typeMatches(actualContext) && valuesMatch(actualContext)
  }

  private def typeMatches(actualContext: ArrayInitializerValuesRenderContext) = {
    new TreeMatcher[Type](expectedContext.tpe).matches(actualContext.tpe)
  }

  private def valuesMatch(actualContext: ArrayInitializerValuesRenderContext) = {
    new ListMatcher(expectedContext.values, new TreeMatcher[Term](_)).matches(actualContext.values)
  }

  override def toString: String = s"Matcher for: $expectedContext"
}

object ArrayInitializerValuesRenderContextMockitoMatcher {
  def eqArrayInitializerValuesRenderContext(expectedContext: ArrayInitializerValuesRenderContext): ArrayInitializerValuesRenderContext =
    argThat(new ArrayInitializerValuesRenderContextMockitoMatcher(expectedContext))
}

