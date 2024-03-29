package io.github.effiban.scala2java.core.renderers.matchers

import io.github.effiban.scala2java.core.renderers.contexts.CtorSecondaryRenderContext
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher
import org.mockito.ArgumentMatcher
import org.mockito.ArgumentMatchers.argThat

class CtorSecondaryRenderContextMockitoMatcher(expectedContext: CtorSecondaryRenderContext) extends ArgumentMatcher[CtorSecondaryRenderContext] {

  override def matches(actualContext: CtorSecondaryRenderContext): Boolean = {
    classNamesMatch(actualContext) && javaModifiersMatch(actualContext)
  }

  private def classNamesMatch(actualContext: CtorSecondaryRenderContext) = {
    new TreeMatcher(expectedContext.className).matches(actualContext.className)
  }

  private def javaModifiersMatch(actualContext: CtorSecondaryRenderContext): Boolean =
    actualContext.javaModifiers == expectedContext.javaModifiers

  override def toString: String = s"Matcher for: $expectedContext"
}

object CtorSecondaryRenderContextMockitoMatcher {
  def eqCtorSecondaryRenderContext(expectedContext: CtorSecondaryRenderContext): CtorSecondaryRenderContext =
    argThat(new CtorSecondaryRenderContextMockitoMatcher(expectedContext))
}

