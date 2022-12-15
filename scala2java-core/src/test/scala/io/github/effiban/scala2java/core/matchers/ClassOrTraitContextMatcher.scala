package io.github.effiban.scala2java.core.matchers

import io.github.effiban.scala2java.core.contexts.ClassOrTraitContext
import io.github.effiban.scala2java.test.utils.matchers.{ListMatcher, TreeMatcher}
import org.mockito.ArgumentMatcher
import org.mockito.ArgumentMatchers.argThat

import scala.meta.Name

class ClassOrTraitContextMatcher(expectedContext: ClassOrTraitContext) extends ArgumentMatcher[ClassOrTraitContext] {

  override def matches(actualContext: ClassOrTraitContext): Boolean = {
    actualContext.javaScope == expectedContext.javaScope && permittedSubTypeNamesMatch(actualContext)
  }

  private def permittedSubTypeNamesMatch(actualContext: ClassOrTraitContext): Boolean = {
    new ListMatcher(expectedContext.permittedSubTypeNames, new TreeMatcher[Name](_)).matches(actualContext.permittedSubTypeNames)
  }

  override def toString: String = s"Matcher for: $expectedContext"
}

object ClassOrTraitContextMatcher {
  def eqClassOrTraitContext(expectedContext: ClassOrTraitContext): ClassOrTraitContext =
    argThat(new ClassOrTraitContextMatcher(expectedContext))
}

