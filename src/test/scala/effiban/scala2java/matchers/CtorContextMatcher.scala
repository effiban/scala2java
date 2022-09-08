package effiban.scala2java.matchers

import effiban.scala2java.entities.CtorContext
import org.mockito.ArgumentMatcher
import org.mockito.ArgumentMatchers.argThat

import scala.meta.Init

class CtorContextMatcher(expectedCtorContext: CtorContext) extends ArgumentMatcher[CtorContext] {

  override def matches(actualCtorContext: CtorContext): Boolean = {
    classNameMatches(actualCtorContext) && initsMatch(actualCtorContext)
  }

  private def classNameMatches(actualClassInfo: CtorContext) = {
    new TreeMatcher(expectedCtorContext.className).matches(actualClassInfo.className)
  }

  private def initsMatch(actualCtorContext: CtorContext) = {
   new ListMatcher(expectedCtorContext.inits, new TreeMatcher[Init](_)).matches(actualCtorContext.inits)
  }

  override def toString: String = s"Matcher for: $expectedCtorContext"
}

object CtorContextMatcher {
  def eqCtorContext(expectedCtorContext: CtorContext): CtorContext = argThat(new CtorContextMatcher(expectedCtorContext))
}

