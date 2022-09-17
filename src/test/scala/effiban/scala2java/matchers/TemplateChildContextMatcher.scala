package effiban.scala2java.matchers

import effiban.scala2java.contexts.TemplateChildContext
import org.mockito.ArgumentMatcher
import org.mockito.ArgumentMatchers.argThat

class TemplateChildContextMatcher(expectedContext: TemplateChildContext) extends ArgumentMatcher[TemplateChildContext] {

  override def matches(actualContext: TemplateChildContext): Boolean = {
    actualContext.javaScope == expectedContext.javaScope && maybeCtorContextMatches(actualContext)
  }

  private def maybeCtorContextMatches(actualContext: TemplateChildContext) = {
    new OptionMatcher(expectedContext.maybeCtorContext, new CtorContextMatcher(_)).matches(actualContext.maybeCtorContext)
  }

  override def toString: String = s"Matcher for: $expectedContext"
}

object TemplateChildContextMatcher {
  def eqTemplateChildContext(expectedContext: TemplateChildContext): TemplateChildContext = argThat(new TemplateChildContextMatcher(expectedContext))
}

