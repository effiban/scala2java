package effiban.scala2java.matchers

import effiban.scala2java.contexts.JavaChildScopeContext
import org.mockito.ArgumentMatcher
import org.mockito.ArgumentMatchers.argThat

class JavaChildScopeContextMatcher(expectedContext: JavaChildScopeContext) extends ArgumentMatcher[JavaChildScopeContext] {

  override def matches(actualContext: JavaChildScopeContext): Boolean = {
    scalaTreeMatches(actualContext) && actualContext.javaTreeType == expectedContext.javaTreeType
  }

  private def scalaTreeMatches(actualContext: JavaChildScopeContext) = {
    new TreeMatcher(expectedContext.scalaTree).matches(actualContext.scalaTree)
  }

  override def toString: String = s"Matcher for: $expectedContext"
}

object JavaChildScopeContextMatcher {
  def eqJavaChildScopeContext(expectedContext: JavaChildScopeContext): JavaChildScopeContext =
    argThat(new JavaChildScopeContextMatcher(expectedContext))
}

