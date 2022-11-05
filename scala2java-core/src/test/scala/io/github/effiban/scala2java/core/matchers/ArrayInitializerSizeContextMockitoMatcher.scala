package io.github.effiban.scala2java.core.matchers

import io.github.effiban.scala2java.core.contexts.ArrayInitializerSizeContext
import org.mockito.ArgumentMatcher
import org.mockito.ArgumentMatchers.argThat

class ArrayInitializerSizeContextMockitoMatcher(expectedContext: ArrayInitializerSizeContext) extends ArgumentMatcher[ArrayInitializerSizeContext] {

  override def matches(actualContext: ArrayInitializerSizeContext): Boolean = {
      typeMatches(actualContext) && sizeMatches(actualContext)
  }

  private def typeMatches(actualContext: ArrayInitializerSizeContext) = {
    new TreeMatcher(actualContext.tpe).matches(actualContext.tpe)
  }

  private def sizeMatches(actualContext: ArrayInitializerSizeContext) = {
    new TreeMatcher(actualContext.size).matches(actualContext.size)
  }

  override def toString: String = s"Matcher for: $expectedContext"
}

object ArrayInitializerSizeContextMockitoMatcher {
  def eqArrayInitializerSizeContext(expectedContext: ArrayInitializerSizeContext): ArrayInitializerSizeContext =
    argThat(new ArrayInitializerSizeContextMockitoMatcher(expectedContext))
}

