package io.github.effiban.scala2java.core.matchers

import io.github.effiban.scala2java.core.contexts.ArrayInitializerTypedValuesContext
import io.github.effiban.scala2java.test.utils.matchers.{ListMatcher, TreeMatcher}
import org.mockito.ArgumentMatcher
import org.mockito.ArgumentMatchers.argThat

import scala.meta.{Term, Type}

class ArrayInitializerTypedValuesContextMockitoMatcher(expectedContext: ArrayInitializerTypedValuesContext)
  extends ArgumentMatcher[ArrayInitializerTypedValuesContext] {

  override def matches(actualContext: ArrayInitializerTypedValuesContext): Boolean = {
      typeMatches(actualContext) && valuesMatch(actualContext)
  }

  private def typeMatches(actualContext: ArrayInitializerTypedValuesContext) = {
    new TreeMatcher[Type](expectedContext.tpe).matches(actualContext.tpe)
  }

  private def valuesMatch(actualContext: ArrayInitializerTypedValuesContext) = {
    new ListMatcher(expectedContext.values, new TreeMatcher[Term](_)).matches(actualContext.values)
  }

  override def toString: String = s"Matcher for: $expectedContext"
}

object ArrayInitializerTypedValuesContextMockitoMatcher {
  def eqArrayInitializerTypedValuesContext(expectedContext: ArrayInitializerTypedValuesContext): ArrayInitializerTypedValuesContext =
    argThat(new ArrayInitializerTypedValuesContextMockitoMatcher(expectedContext))
}

