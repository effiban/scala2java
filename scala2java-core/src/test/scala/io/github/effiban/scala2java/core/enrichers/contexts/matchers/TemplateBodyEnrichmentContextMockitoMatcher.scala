package io.github.effiban.scala2java.core.enrichers.contexts.matchers

import io.github.effiban.scala2java.core.enrichers.contexts.TemplateBodyEnrichmentContext
import io.github.effiban.scala2java.test.utils.matchers.{OptionMatcher, TreeMatcher}
import org.mockito.ArgumentMatcher
import org.mockito.ArgumentMatchers.argThat

import scala.meta.Type

class TemplateBodyEnrichmentContextMockitoMatcher(expectedContext: TemplateBodyEnrichmentContext)
  extends ArgumentMatcher[TemplateBodyEnrichmentContext] {

  override def matches(actualContext: TemplateBodyEnrichmentContext): Boolean = {
    actualContext.javaScope == expectedContext.javaScope && maybeClassNameMatches(actualContext)
  }

  private def maybeClassNameMatches(actualContext: TemplateBodyEnrichmentContext) = {
    new OptionMatcher(expectedContext.maybeClassName, new TreeMatcher[Type.Name](_)).matches(actualContext.maybeClassName)
  }

  override def toString: String = s"Matcher for: $expectedContext"
}

object TemplateBodyEnrichmentContextMockitoMatcher {
  def eqTemplateBodyEnrichmentContext(expectedContext: TemplateBodyEnrichmentContext): TemplateBodyEnrichmentContext =
    argThat(new TemplateBodyEnrichmentContextMockitoMatcher(expectedContext))
}

