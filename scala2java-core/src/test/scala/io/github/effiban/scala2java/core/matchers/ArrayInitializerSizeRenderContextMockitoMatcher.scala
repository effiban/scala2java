package io.github.effiban.scala2java.core.matchers

import io.github.effiban.scala2java.core.contexts.ArrayInitializerSizeRenderContext
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher
import org.mockito.ArgumentMatcher
import org.mockito.ArgumentMatchers.argThat

class ArrayInitializerSizeRenderContextMockitoMatcher(expectedContext: ArrayInitializerSizeRenderContext)
  extends ArgumentMatcher[ArrayInitializerSizeRenderContext] {

  override def matches(actualContext: ArrayInitializerSizeRenderContext): Boolean = {
      typeMatches(actualContext) && sizeMatches(actualContext)
  }

  private def typeMatches(actualContext: ArrayInitializerSizeRenderContext) = {
    new TreeMatcher(actualContext.tpe).matches(actualContext.tpe)
  }

  private def sizeMatches(actualContext: ArrayInitializerSizeRenderContext) = {
    new TreeMatcher(actualContext.size).matches(actualContext.size)
  }

  override def toString: String = s"Matcher for: $expectedContext"
}

object ArrayInitializerSizeRenderContextMockitoMatcher {
  def eqArrayInitializerSizeRenderContext(expectedContext: ArrayInitializerSizeRenderContext): ArrayInitializerSizeRenderContext =
    argThat(new ArrayInitializerSizeRenderContextMockitoMatcher(expectedContext))
}

