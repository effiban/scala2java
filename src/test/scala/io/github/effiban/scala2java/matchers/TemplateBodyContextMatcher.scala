package io.github.effiban.scala2java.matchers

import io.github.effiban.scala2java.contexts.TemplateBodyContext
import org.mockito.ArgumentMatcher
import org.mockito.ArgumentMatchers.argThat

import scala.meta.{Ctor, Init, Type}

class TemplateBodyContextMatcher(expectedContext: TemplateBodyContext) extends ArgumentMatcher[TemplateBodyContext] {

  override def matches(actualContext: TemplateBodyContext): Boolean = {
    actualContext.javaScope == expectedContext.javaScope &&
      classNameMatches(actualContext) &&
      explicitPrimaryCtorMatches(actualContext) &&
      initsMatch(actualContext)
  }

  private def classNameMatches(actualContext: TemplateBodyContext) = {
    new OptionMatcher(expectedContext.maybeClassName, new TreeMatcher[Type.Name](_))
      .matches(actualContext.maybeClassName)
  }

  private def explicitPrimaryCtorMatches(actualContext: TemplateBodyContext) = {
    new OptionMatcher(expectedContext.maybePrimaryCtor, new TreeMatcher[Ctor.Primary](_))
      .matches(actualContext.maybePrimaryCtor)
  }

  private def initsMatch(actualContext: TemplateBodyContext) = {
    new ListMatcher(expectedContext.inits, new TreeMatcher[Init](_)).matches(actualContext.inits)
  }

  override def toString: String = s"Matcher for: $expectedContext"
}

object TemplateBodyContextMatcher {
  def eqTemplateBodyContext(expectedContext: TemplateBodyContext): TemplateBodyContext = argThat(new TemplateBodyContextMatcher(expectedContext))
}

