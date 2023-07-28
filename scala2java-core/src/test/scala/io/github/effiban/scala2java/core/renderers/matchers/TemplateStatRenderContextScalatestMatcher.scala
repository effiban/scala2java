package io.github.effiban.scala2java.core.renderers.matchers

import io.github.effiban.scala2java.core.renderers.contexts._
import org.scalatest.matchers.{MatchResult, Matcher}

class TemplateStatRenderContextScalatestMatcher(expectedContext: TemplateStatRenderContext) extends Matcher[TemplateStatRenderContext] {

  override def apply(actualContext: TemplateStatRenderContext): MatchResult = {
    val matches = true // TODO

    MatchResult(matches,
      s"Actual context: $actualContext is NOT the same as expected context: $expectedContext",
      s"Actual context: $actualContext the same as expected context: $expectedContext"
    )
  }

  override def toString: String = s"Matcher for: $expectedContext"
}

object TemplateStatRenderContextScalatestMatcher {
  def equalTemplateStatRenderContext(expectedContext: TemplateStatRenderContext): TemplateStatRenderContextScalatestMatcher =
    new TemplateStatRenderContextScalatestMatcher(expectedContext)
}

