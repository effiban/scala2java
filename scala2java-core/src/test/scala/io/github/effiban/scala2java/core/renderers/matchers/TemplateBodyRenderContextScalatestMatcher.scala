package io.github.effiban.scala2java.core.renderers.matchers

import io.github.effiban.scala2java.core.renderers.contexts.TemplateBodyRenderContext
import io.github.effiban.scala2java.test.utils.matchers.TreeKeyedMapScalatestMatcher
import org.scalatest.matchers.{MatchResult, Matcher}

class TemplateBodyRenderContextScalatestMatcher(expectedContext: TemplateBodyRenderContext) extends Matcher[TemplateBodyRenderContext] {

  override def apply(actualContext: TemplateBodyRenderContext): MatchResult = {
    val mapMatcher = new TreeKeyedMapScalatestMatcher(expectedContext.statContextMap, new TemplateStatRenderContextScalatestMatcher(_))
    val matches = mapMatcher(actualContext.statContextMap).matches

    MatchResult(matches,
      s"Actual context: $actualContext is NOT the same as expected context: $expectedContext",
      s"Actual context: $actualContext the same as expected context: $expectedContext"
    )
  }

  override def toString: String = s"Matcher for: $expectedContext"
}

object TemplateBodyRenderContextScalatestMatcher {
  def equalTemplateBodyRenderContext(expectedContext: TemplateBodyRenderContext): TemplateBodyRenderContextScalatestMatcher =
    new TemplateBodyRenderContextScalatestMatcher(expectedContext)
}

