package io.github.effiban.scala2java.core.renderers.matchers

import io.github.effiban.scala2java.core.renderers.contexts.TemplateBodyRenderContext
import io.github.effiban.scala2java.test.utils.matchers.TreeKeyedMapMatcher
import org.mockito.ArgumentMatcher
import org.mockito.ArgumentMatchers.argThat

class TemplateBodyRenderContextMatcher(expectedContext: TemplateBodyRenderContext) extends ArgumentMatcher[TemplateBodyRenderContext] {

  override def matches(actualContext: TemplateBodyRenderContext): Boolean = {
    new TreeKeyedMapMatcher(expectedContext.statContextMap, new TemplateStatRenderContextMatcher(_))
      .matches(actualContext.statContextMap)
  }

  override def toString: String = s"Matcher for: $expectedContext"
}

object TemplateBodyRenderContextMatcher {
  def eqTemplateBodyRenderContext(expectedContext: TemplateBodyRenderContext): TemplateBodyRenderContext =
    argThat(new TemplateBodyRenderContextMatcher(expectedContext))
}

