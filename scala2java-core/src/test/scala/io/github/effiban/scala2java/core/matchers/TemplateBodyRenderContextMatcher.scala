package io.github.effiban.scala2java.core.matchers

import io.github.effiban.scala2java.core.renderers.contexts.TemplateBodyRenderContext
import org.mockito.ArgumentMatcher
import org.mockito.ArgumentMatchers.argThat

class TemplateBodyRenderContextMatcher(expectedContext: TemplateBodyRenderContext) extends ArgumentMatcher[TemplateBodyRenderContext] {

  override def matches(actualContext: TemplateBodyRenderContext): Boolean = {
    statsMatch(actualContext) && statContextsMatch(actualContext)
  }

  override def toString: String = s"Matcher for: $expectedContext"

  private def statsMatch(actualContext: TemplateBodyRenderContext) = {
    actualContext.statContextMap.keySet == expectedContext.statContextMap.keySet
  }

  private def statContextsMatch(actualContext: TemplateBodyRenderContext): Boolean = {
    expectedContext.statContextMap.forall { case (stat, expectedStatContext) =>
      actualContext.statContextMap(stat) == expectedStatContext
    }
  }
}

object TemplateBodyRenderContextMatcher {
  def eqTemplateBodyRenderContext(expectedContext: TemplateBodyRenderContext): TemplateBodyRenderContext =
    argThat(new TemplateBodyRenderContextMatcher(expectedContext))
}

