package io.github.effiban.scala2java.test.utils.matchers

import org.mockito.ArgumentMatcher
import org.mockito.ArgumentMatchers.argThat
import org.mockito.matchers.EqTo

/** A Mockito [[ArgumentMatcher]] for comparing two `Option`s, expecting both to exist and using a given nested matcher */
class SomeMatcher[T](expectedVal: T,
                     valMatcher: T => ArgumentMatcher[T] = (value: T) => EqTo[T](value)) extends ArgumentMatcher[Option[T]] {

  override def matches(actualOption: Option[T]): Boolean = {
    actualOption match {
      case Some(actualVal) => valMatcher(expectedVal).matches(actualVal)
      case _ => false
    }
  }

  override def toString: String = s"Matcher for: Some($expectedVal)"
}

object SomeMatcher {

  /** A convenience reporter method (using `argThat`) for [[SomeMatcher]] */
  def eqSome[T](expectedVal: T,
                valMatcherGenerator: T => ArgumentMatcher[T] = (value: T) => EqTo[T](value)): Option[T] =
    argThat(new SomeMatcher[T](expectedVal, valMatcherGenerator))
}



