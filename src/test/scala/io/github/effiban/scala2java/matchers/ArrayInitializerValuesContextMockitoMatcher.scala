package io.github.effiban.scala2java.matchers

import io.github.effiban.scala2java.contexts.ArrayInitializerValuesContext
import org.mockito.ArgumentMatcher
import org.mockito.ArgumentMatchers.argThat

import scala.meta.{Term, Type}

class ArrayInitializerValuesContextMockitoMatcher(expectedContext: ArrayInitializerValuesContext) extends ArgumentMatcher[ArrayInitializerValuesContext] {

  override def matches(actualContext: ArrayInitializerValuesContext): Boolean = {
      maybeTypeMatches(actualContext) && valuesMatch(actualContext)
  }

  private def maybeTypeMatches(actualContext: ArrayInitializerValuesContext) = {
    new OptionMatcher[Type](actualContext.maybeType, new TreeMatcher[Type](_)).matches(actualContext.maybeType)
  }

  private def valuesMatch(actualContext: ArrayInitializerValuesContext) = {
    new ListMatcher(expectedContext.values, new TreeMatcher[Term](_)).matches(actualContext.values)
  }

  override def toString: String = s"Matcher for: $expectedContext"
}

object ArrayInitializerValuesContextMockitoMatcher {
  def eqArrayInitializerValuesContext(expectedContext: ArrayInitializerValuesContext): ArrayInitializerValuesContext =
    argThat(new ArrayInitializerValuesContextMockitoMatcher(expectedContext))
}

