package io.github.effiban.scala2java.core.matchers

import io.github.effiban.scala2java.core.contexts.CtorRequiredResolutionContext
import io.github.effiban.scala2java.test.utils.matchers.{ListMatcher, TreeMatcher}
import org.mockito.ArgumentMatcher
import org.mockito.ArgumentMatchers.argThat

import scala.meta.{Init, Stat, Term}

class CtorRequiredResolutionContextMatcher(expectedContext: CtorRequiredResolutionContext) extends ArgumentMatcher[CtorRequiredResolutionContext] {

  override def matches(actualContext: CtorRequiredResolutionContext): Boolean = {
      initsMatch(actualContext) &&
        termsMatch(actualContext) &&
        otherStatsMatch(actualContext)
  }

  private def initsMatch(actualContext: CtorRequiredResolutionContext) = {
    new ListMatcher(expectedContext.inits, new TreeMatcher[Init](_)).matches(actualContext.inits)
  }

  private def termsMatch(actualContext: CtorRequiredResolutionContext) = {
    new ListMatcher(expectedContext.terms, new TreeMatcher[Term](_)).matches(actualContext.terms)
  }

  private def otherStatsMatch(actualContext: CtorRequiredResolutionContext) = {
    new ListMatcher(expectedContext.otherStats, new TreeMatcher[Stat](_)).matches(actualContext.otherStats)
  }

  override def toString: String = s"Matcher for: $expectedContext"
}

object CtorRequiredResolutionContextMatcher {
  def eqCtorRequiredResolutionContext(expectedContext: CtorRequiredResolutionContext): CtorRequiredResolutionContext =
    argThat(new CtorRequiredResolutionContextMatcher(expectedContext))
}

