package io.github.effiban.scala2java.core.renderers.matchers

import io.github.effiban.scala2java.core.renderers.contexts._
import org.scalatest.matchers.{MatchResult, Matcher}

class StatRenderContextScalatestMatcher(expectedContext: StatRenderContext) extends Matcher[StatRenderContext] {

  override def apply(actualContext: StatRenderContext): MatchResult = {
    val matches = (actualContext, expectedContext) match {
      case (actualPkgContext: PkgRenderContext, expectedPkgContext: PkgRenderContext) =>
        new PkgRenderContextScalatestMatcher(expectedPkgContext)(actualPkgContext).matches
      case (actualDeclContext: DeclRenderContext, expectedDeclContext: DeclRenderContext) =>
        new DeclRenderContextScalatestMatcher(expectedDeclContext)(actualDeclContext).matches
      case (actualDefnContext: DefnRenderContext, expectedDefnContext: DefnRenderContext) =>
        new DefnRenderContextScalatestMatcher(expectedDefnContext)(actualDefnContext).matches
      // TODO support the rest of the hierarchy
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

