package io.github.effiban.scala2java.core.renderers.matchers

import io.github.effiban.scala2java.core.renderers.contexts.PkgRenderContext
import io.github.effiban.scala2java.test.utils.matchers.TreeKeyedMapMatcher
import org.mockito.ArgumentMatcher
import org.mockito.ArgumentMatchers.argThat

class PkgRenderContextMatcher(expectedContext: PkgRenderContext) extends ArgumentMatcher[PkgRenderContext] {

  override def matches(actualContext: PkgRenderContext): Boolean = {
    new TreeKeyedMapMatcher(expectedContext.statContextMap, new StatRenderContextMatcher(_))
      .matches(actualContext.statContextMap)
  }

  override def toString: String = s"Matcher for: $expectedContext"
}

object PkgRenderContextMatcher {
  def eqPkgRenderContext(expectedContext: PkgRenderContext): PkgRenderContext =
    argThat(new PkgRenderContextMatcher(expectedContext))
}

