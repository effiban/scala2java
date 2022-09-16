package effiban.scala2java.matchers

import effiban.scala2java.contexts.DefnDefContext
import org.mockito.ArgumentMatcher
import org.mockito.ArgumentMatchers.argThat

import scala.meta.Init

class DefnDefContextMatcher(expectedContext: DefnDefContext) extends ArgumentMatcher[DefnDefContext] {

  override def matches(actualContext: DefnDefContext): Boolean = {
    actualContext.javaScope == expectedContext.javaScope && maybeInitMatches(actualContext)
  }

  private def maybeInitMatches(actualContext: DefnDefContext) = {
    new OptionMatcher[Init](expectedContext.maybeInit, new TreeMatcher[Init](_)).matches(actualContext.maybeInit)
  }

  override def toString: String = s"Matcher for: $expectedContext"
}

object DefnDefContextMatcher {
  def eqDefnDefContext(expectedContext: DefnDefContext): DefnDefContext = argThat(new DefnDefContextMatcher(expectedContext))
}

