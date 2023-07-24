package io.github.effiban.scala2java.core.renderers.matchers

import io.github.effiban.scala2java.core.renderers.contexts._
import org.mockito.ArgumentMatcher
import org.mockito.ArgumentMatchers.argThat

class TemplateStatRenderContextMockitoMatcher(expectedContext: TemplateStatRenderContext) extends ArgumentMatcher[TemplateStatRenderContext] {

  override def matches(actualContext: TemplateStatRenderContext): Boolean = {
    (actualContext, expectedContext) match {
      case (actualDefnContext: DefnRenderContext, expectedDefnContext: DefnRenderContext) =>
        new DefnRenderContextMockitoMatcher(expectedDefnContext).matches(actualDefnContext)
      case (actualCtorContext: CtorSecondaryRenderContext, expectedCtorContext: CtorSecondaryRenderContext) =>
        new CtorSecondaryRenderContextMockitoMatcher(expectedCtorContext).matches(actualCtorContext)
      case (anActualContext, anExpectedContext) => anActualContext == anExpectedContext
    }
  }

  override def toString: String = s"Matcher for: $expectedContext"
}

object TemplateStatRenderContextMockitoMatcher {
  def eqTemplateStatRenderContext(expectedContext: TemplateStatRenderContext): TemplateStatRenderContext =
    argThat(new TemplateStatRenderContextMockitoMatcher(expectedContext))
}

