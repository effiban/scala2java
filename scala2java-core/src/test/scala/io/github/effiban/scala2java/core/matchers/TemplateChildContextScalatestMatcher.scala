package io.github.effiban.scala2java.core.matchers

import io.github.effiban.scala2java.core.contexts.TemplateChildContext
import io.github.effiban.scala2java.test.utils.matchers.{ListMatcher, OptionMatcher, TreeMatcher}
import org.scalatest.matchers.{MatchResult, Matcher}

import scala.meta.{Init, Term, Type}

class TemplateChildContextScalatestMatcher(expectedContext: TemplateChildContext) extends Matcher[TemplateChildContext] {

  override def apply(actualContext: TemplateChildContext): MatchResult = {

    val matches = actualContext.javaScope == expectedContext.javaScope &&
      maybeClassNameMatches(actualContext) &&
      initsMatch(actualContext) &&
      ctorTermsMatch(actualContext)

    MatchResult(matches,
      s"Actual context: $actualContext is NOT the same as expected context: $expectedContext",
      s"Actual context: $actualContext the same as expected context: $expectedContext"
    )
  }

  private def maybeClassNameMatches(actualContext: TemplateChildContext) = {
    new OptionMatcher(expectedContext.maybeClassName, new TreeMatcher[Type.Name](_)).matches(actualContext.maybeClassName)
  }

  private def initsMatch(actualCtorContext: TemplateChildContext) = {
    new ListMatcher(expectedContext.inits, new TreeMatcher[Init](_)).matches(actualCtorContext.inits)
  }

  private def ctorTermsMatch(actualCtorContext: TemplateChildContext) = {
    new ListMatcher(expectedContext.ctorTerms, new TreeMatcher[Term](_)).matches(actualCtorContext.ctorTerms)
  }
}

object TemplateChildContextScalatestMatcher {
  def equalTemplateChildContext(expectedContext: TemplateChildContext): Matcher[TemplateChildContext] =
    new TemplateChildContextScalatestMatcher(expectedContext)
}

