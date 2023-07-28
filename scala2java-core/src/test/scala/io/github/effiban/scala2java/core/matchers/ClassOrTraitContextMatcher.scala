package io.github.effiban.scala2java.core.matchers

import io.github.effiban.scala2java.core.contexts.ClassOrTraitContext
import org.mockito.ArgumentMatcher
import org.mockito.ArgumentMatchers.argThat

class ClassOrTraitContextMatcher(expectedContext: ClassOrTraitContext) extends ArgumentMatcher[ClassOrTraitContext] {

  override def matches(actualContext: ClassOrTraitContext): Boolean = {
    actualContext.javaScope == expectedContext.javaScope
  }

  override def toString: String = s"Matcher for: $expectedContext"
}

object ClassOrTraitContextMatcher {
  def eqClassOrTraitContext(expectedContext: ClassOrTraitContext): ClassOrTraitContext =
    argThat(new ClassOrTraitContextMatcher(expectedContext))
}

