package io.github.effiban.scala2java.core.renderers.matchers

import io.github.effiban.scala2java.core.renderers.contexts._
import org.mockito.ArgumentMatcher
import org.mockito.ArgumentMatchers.argThat

class StatRenderContextMockitoMatcher(expectedContext: StatRenderContext) extends ArgumentMatcher[StatRenderContext] {

  override def matches(actualContext: StatRenderContext): Boolean = {
    (actualContext, expectedContext) match {
      case (actualTemplateStatContext: TemplateStatRenderContext, expectedTemplateStatContext: TemplateStatRenderContext) =>
        new TemplateStatRenderContextMockitoMatcher(expectedTemplateStatContext).matches(actualTemplateStatContext)
      case (actualPkgContext: PkgRenderContext, expectedPkgContext: PkgRenderContext) =>
        new PkgRenderContextMockitoMatcher(expectedPkgContext).matches(actualPkgContext)
      case (anActualContext, anExpectedContext) => anActualContext == anExpectedContext
    }
  }

  override def toString: String = s"Matcher for: $expectedContext"
}

object StatRenderContextMockitoMatcher {
  def eqStatRenderContext(expectedContext: StatRenderContext): StatRenderContext =
    argThat(new StatRenderContextMockitoMatcher(expectedContext))
}

