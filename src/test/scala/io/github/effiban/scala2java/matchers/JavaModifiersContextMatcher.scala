package io.github.effiban.scala2java.matchers

import io.github.effiban.scala2java.contexts.JavaModifiersContext
import org.mockito.ArgumentMatcher
import org.mockito.ArgumentMatchers.argThat

import scala.meta.Mod

class JavaModifiersContextMatcher(expectedContext: JavaModifiersContext) extends ArgumentMatcher[JavaModifiersContext] {

  override def matches(actualContext: JavaModifiersContext): Boolean = {
    scalaTreeMatches(actualContext) &&
      scalaModsMatch(actualContext) &&
      actualContext.javaTreeType == expectedContext.javaTreeType
      actualContext.javaScope == expectedContext.javaScope
  }

  private def scalaTreeMatches(actualContext: JavaModifiersContext) = {
    new TreeMatcher(expectedContext.scalaTree).matches(actualContext.scalaTree)
  }

  private def scalaModsMatch(actualContext: JavaModifiersContext): Boolean = {
    new ListMatcher(expectedContext.scalaMods, new TreeMatcher[Mod](_)).matches(actualContext.scalaMods)
  }

  override def toString: String = s"Matcher for: $expectedContext"
}

object JavaModifiersContextMatcher {
  def eqJavaModifiersContext(expectedContext: JavaModifiersContext): JavaModifiersContext =
    argThat(new JavaModifiersContextMatcher(expectedContext))
}

