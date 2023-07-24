package io.github.effiban.scala2java.core.renderers.matchers

import io.github.effiban.scala2java.core.renderers.contexts.PkgRenderContext
import io.github.effiban.scala2java.test.utils.matchers.TreeKeyedMapMockitoMatcher
import org.mockito.ArgumentMatcher
import org.mockito.ArgumentMatchers.argThat

class PkgRenderContextMockitoMatcher(expectedContext: PkgRenderContext) extends ArgumentMatcher[PkgRenderContext] {

  override def matches(actualContext: PkgRenderContext): Boolean = {
    new TreeKeyedMapMockitoMatcher(expectedContext.statContextMap, new StatRenderContextMockitoMatcher(_))
      .matches(actualContext.statContextMap)
  }

  override def toString: String = s"Matcher for: $expectedContext"
}

object PkgRenderContextMockitoMatcher {
  def eqPkgRenderContext(expectedContext: PkgRenderContext): PkgRenderContext =
    argThat(new PkgRenderContextMockitoMatcher(expectedContext))
}

