package io.github.effiban.scala2java.core.matchers

import io.github.effiban.scala2java.core.contexts.InternalTermNameTransformationContext
import io.github.effiban.scala2java.test.utils.matchers.{OptionMatcher, TreeMatcher}
import org.mockito.ArgumentMatcher
import org.mockito.ArgumentMatchers.argThat

import scala.meta.Tree

class InternalTermNameTransformationContextMatcher(expectedContext: InternalTermNameTransformationContext) extends ArgumentMatcher[InternalTermNameTransformationContext] {

  override def matches(actualContext: InternalTermNameTransformationContext): Boolean = {
    new OptionMatcher[Tree](expectedContext.maybeParent, new TreeMatcher(_)).matches(actualContext.maybeParent)
  }

  override def toString: String = s"Matcher for: $expectedContext"
}

object InternalTermNameTransformationContextMatcher {
  def eqInternalTermNameTransformationContext(expectedContext: InternalTermNameTransformationContext): InternalTermNameTransformationContext =
    argThat(new InternalTermNameTransformationContextMatcher(expectedContext))
}

