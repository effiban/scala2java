package io.github.effiban.scala2java.core.enrichers.contexts.matchers

import io.github.effiban.scala2java.core.enrichers.contexts.TemplateEnrichmentContext
import io.github.effiban.scala2java.test.utils.matchers.{OptionMatcher, TreeMatcher}
import org.mockito.ArgumentMatcher
import org.mockito.ArgumentMatchers.argThat

import scala.meta.Type

class TemplateEnrichmentContextMockitoMatcher(expectedContext: TemplateEnrichmentContext)
  extends ArgumentMatcher[TemplateEnrichmentContext] {

  override def matches(actualContext: TemplateEnrichmentContext): Boolean = {
    actualContext.javaScope == expectedContext.javaScope && maybeClassNameMatches(actualContext)
  }

  private def maybeClassNameMatches(actualContext: TemplateEnrichmentContext) = {
    new OptionMatcher(expectedContext.maybeClassName, new TreeMatcher[Type.Name](_)).matches(actualContext.maybeClassName)
  }

  override def toString: String = s"Matcher for: $expectedContext"
}

object TemplateEnrichmentContextMockitoMatcher {
  def eqTemplateEnrichmentContext(expectedContext: TemplateEnrichmentContext): TemplateEnrichmentContext =
    argThat(new TemplateEnrichmentContextMockitoMatcher(expectedContext))
}

