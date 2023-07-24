package io.github.effiban.scala2java.core.renderers.matchers

import io.github.effiban.scala2java.core.renderers.contexts.TemplateBodyRenderContext
import io.github.effiban.scala2java.test.utils.matchers.TreeKeyedMapMockitoMatcher
import org.mockito.ArgumentMatcher
import org.mockito.ArgumentMatchers.argThat

class TemplateBodyRenderContextMockitoMatcher(expectedContext: TemplateBodyRenderContext) extends ArgumentMatcher[TemplateBodyRenderContext] {

  override def matches(actualContext: TemplateBodyRenderContext): Boolean = {
    new TreeKeyedMapMockitoMatcher(expectedContext.statContextMap, new TemplateStatRenderContextMockitoMatcher(_))
      .matches(actualContext.statContextMap)
  }

  override def toString: String = s"Matcher for: $expectedContext"
}

object TemplateBodyRenderContextMockitoMatcher {
  def eqTemplateBodyRenderContext(expectedContext: TemplateBodyRenderContext): TemplateBodyRenderContext =
    argThat(new TemplateBodyRenderContextMockitoMatcher(expectedContext))
}

