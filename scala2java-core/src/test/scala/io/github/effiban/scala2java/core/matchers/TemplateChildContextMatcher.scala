package io.github.effiban.scala2java.core.matchers

import io.github.effiban.scala2java.core.contexts.TemplateChildContext
import io.github.effiban.scala2java.test.utils.matchers.{ListMatcher, OptionMatcher, TreeMatcher}
import org.mockito.ArgumentMatcher
import org.mockito.ArgumentMatchers.argThat

import scala.meta.{Init, Term, Type}

class TemplateChildContextMatcher(expectedContext: TemplateChildContext) extends ArgumentMatcher[TemplateChildContext] {

  override def matches(actualContext: TemplateChildContext): Boolean = {
    actualContext.javaScope == expectedContext.javaScope &&
      maybeClassNameMatches(actualContext) &&
      initsMatch(actualContext) &&
      ctorTermsMatch(actualContext)
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


  override def toString: String = s"Matcher for: $expectedContext"
}

object TemplateChildContextMatcher {
  def eqTemplateChildContext(expectedContext: TemplateChildContext): TemplateChildContext = argThat(new TemplateChildContextMatcher(expectedContext))
}

