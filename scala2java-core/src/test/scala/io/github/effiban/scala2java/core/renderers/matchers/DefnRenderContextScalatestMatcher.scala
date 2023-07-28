package io.github.effiban.scala2java.core.renderers.matchers

import io.github.effiban.scala2java.core.renderers.contexts._
import org.scalatest.matchers.{MatchResult, Matcher}

class DefnRenderContextScalatestMatcher(expectedContext: DefnRenderContext) extends Matcher[DefnRenderContext] {
  override def apply(actualContext: DefnRenderContext): MatchResult = {
    val matches = (actualContext, expectedContext) match {
      case (actualTraitContext: TraitRenderContext, expectedTraitContext: TraitRenderContext) =>
        new TraitRenderContextScalatestMatcher(expectedTraitContext)(actualTraitContext).matches
      case (actualCaseClassContext: CaseClassRenderContext, expectedCaseClassContext: CaseClassRenderContext) =>
        new CaseClassRenderContextScalatestMatcher(expectedCaseClassContext)(actualCaseClassContext).matches
      case (actualRegularClassContext: RegularClassRenderContext, expectedRegularClassContext: RegularClassRenderContext) =>
        new RegularClassRenderContextScalatestMatcher(expectedRegularClassContext)(actualRegularClassContext).matches
      case (actualObjectContext: ObjectRenderContext, expectedObjectContext: ObjectRenderContext) =>
        new ObjectRenderContextScalatestMatcher(expectedObjectContext)(actualObjectContext).matches
      case (anActualContext, anExpectedContext) => anActualContext == anExpectedContext
    }

    MatchResult(matches,
      s"Actual context: $actualContext is NOT the same as expected context: $expectedContext",
      s"Actual context: $actualContext the same as expected context: $expectedContext"
    )
  }

  override def toString: String = s"Matcher for: $expectedContext"
}

object DefnRenderContextScalatestMatcher {
  def equalDefnRenderContext(expectedContext: DefnRenderContext): DefnRenderContextScalatestMatcher =
    new DefnRenderContextScalatestMatcher(expectedContext)
}
