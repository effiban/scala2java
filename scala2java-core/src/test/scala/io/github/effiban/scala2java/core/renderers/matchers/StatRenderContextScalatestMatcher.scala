package io.github.effiban.scala2java.core.renderers.matchers

import io.github.effiban.scala2java.core.renderers.contexts._
import org.scalatest.matchers.{MatchResult, Matcher}

class StatRenderContextScalatestMatcher(expectedContext: StatRenderContext) extends Matcher[StatRenderContext] {

  override def apply(actualContext: StatRenderContext): MatchResult = {
    val matches = (actualContext, expectedContext) match {
      case (actualPkgContext: PkgRenderContext, expectedPkgContext: PkgRenderContext) =>
        new PkgRenderContextScalatestMatcher(expectedPkgContext)(actualPkgContext).matches
      case (actualTemplateStatContext: TemplateStatRenderContext, expectedTemplateStatContext: TemplateStatRenderContext) =>
        new TemplateStatRenderContextScalatestMatcher(expectedTemplateStatContext)(actualTemplateStatContext).matches
      case (anActualContext, anExpectedContext) => anActualContext == anExpectedContext
    }

    MatchResult(matches,
      s"Actual context: $actualContext is NOT the same as expected context: $expectedContext",
      s"Actual context: $actualContext the same as expected context: $expectedContext"
    )
  }

  override def toString: String = s"Matcher for: $expectedContext"
}

object StatRenderContextScalatestMatcher {
  def equalStatRenderContext(expectedContext: StatRenderContext): StatRenderContextScalatestMatcher =
    new StatRenderContextScalatestMatcher(expectedContext)
}

