package io.github.effiban.scala2java.core.enrichers.contexts.matchers

import io.github.effiban.scala2java.core.enrichers.contexts.CtorSecondaryEnrichmentContext
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher
import org.mockito.ArgumentMatcher
import org.mockito.ArgumentMatchers.argThat

class CtorSecondaryEnrichmentContextMockitoMatcher(expectedContext: CtorSecondaryEnrichmentContext)
  extends ArgumentMatcher[CtorSecondaryEnrichmentContext] {

  override def matches(actualContext: CtorSecondaryEnrichmentContext): Boolean = {
    actualContext.javaScope == expectedContext.javaScope && classNameMatches(actualContext)
  }

  private def classNameMatches(actualContext: CtorSecondaryEnrichmentContext) = {
    new TreeMatcher(expectedContext.className).matches(actualContext.className)
  }

  override def toString: String = s"Matcher for: $expectedContext"
}

object CtorSecondaryEnrichmentContextMockitoMatcher {
  def eqCtorSecondaryEnrichmentContext(expectedContext: CtorSecondaryEnrichmentContext): CtorSecondaryEnrichmentContext =
    argThat(new CtorSecondaryEnrichmentContextMockitoMatcher(expectedContext))
}

