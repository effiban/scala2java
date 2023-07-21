package io.github.effiban.scala2java.core.renderers.matchers

import io.github.effiban.scala2java.core.renderers.contexts._
import org.mockito.ArgumentMatcher
import org.mockito.ArgumentMatchers.argThat

class TemplateStatRenderContextMatcher(expectedContext: TemplateStatRenderContext) extends ArgumentMatcher[TemplateStatRenderContext] {

  override def matches(actualContext: TemplateStatRenderContext): Boolean = {
    (actualContext, expectedContext) match {
      case (actualDefnContext: DefnRenderContext, expectedDefnContext: DefnRenderContext) =>
        new DefnRenderContextMatcher(expectedDefnContext).matches(actualDefnContext)
      case (anActualContext, anExpectedContext) => anActualContext == anExpectedContext
    }
  }

  override def toString: String = s"Matcher for: $expectedContext"
}

object TemplateStatRenderContextMatcher {
  def eqTemplateStatRenderContext(expectedContext: TemplateStatRenderContext): TemplateStatRenderContext =
    argThat(new TemplateStatRenderContextMatcher(expectedContext))
}

