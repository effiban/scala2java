package effiban.scala2java.matchers

import effiban.scala2java.contexts.CtorContext
import org.mockito.ArgumentMatcher
import org.mockito.ArgumentMatchers.argThat

import scala.meta.{Init, Term}

class CtorContextMatcher(expectedContext: CtorContext) extends ArgumentMatcher[CtorContext] {

  override def matches(actualContext: CtorContext): Boolean = {
    actualContext.javaScope == expectedContext.javaScope &&
      classNameMatches(actualContext) &&
      initsMatch(actualContext) &&
      termsMatch(actualContext)
  }

  private def classNameMatches(actualContext: CtorContext) = {
    new TreeMatcher(expectedContext.className).matches(actualContext.className)
  }

  private def initsMatch(actualContext: CtorContext) = {
    new ListMatcher(expectedContext.inits, new TreeMatcher[Init](_)).matches(actualContext.inits)
  }

  private def termsMatch(actualContext: CtorContext) = {
    new ListMatcher(expectedContext.terms, new TreeMatcher[Term](_)).matches(actualContext.terms)
  }

  override def toString: String = s"Matcher for: $expectedContext"
}

object CtorContextMatcher {
  def eqCtorContext(expectedContext: CtorContext): CtorContext = argThat(new CtorContextMatcher(expectedContext))
}

