package io.github.effiban.scala2java.core.matchers

import io.github.effiban.scala2java.core.contexts.JavaTreeTypeContext
import org.mockito.ArgumentMatcher
import org.mockito.ArgumentMatchers.argThat

import scala.meta.Mod

class JavaTreeTypeContextMatcher(expectedContext: JavaTreeTypeContext) extends ArgumentMatcher[JavaTreeTypeContext] {

  override def matches(actualContext: JavaTreeTypeContext): Boolean = {
    scalaTreeMatches(actualContext) &&
      scalaModsMatch(actualContext)
  }

  private def scalaTreeMatches(actualContext: JavaTreeTypeContext) = {
    new TreeMatcher(expectedContext.tree).matches(actualContext.tree)
  }

  private def scalaModsMatch(actualContext: JavaTreeTypeContext): Boolean = {
    new ListMatcher(expectedContext.mods, new TreeMatcher[Mod](_)).matches(actualContext.mods)
  }

  override def toString: String = s"Matcher for: $expectedContext"
}

object JavaTreeTypeContextMatcher {
  def eqJavaTreeTypeContext(expectedContext: JavaTreeTypeContext): JavaTreeTypeContext =
    argThat(new JavaTreeTypeContextMatcher(expectedContext))
}

