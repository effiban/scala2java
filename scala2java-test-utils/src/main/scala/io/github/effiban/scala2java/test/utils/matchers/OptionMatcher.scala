package io.github.effiban.scala2java.test.utils.matchers

import org.mockito.ArgumentMatcher
import org.mockito.ArgumentMatchers.argThat
import org.mockito.matchers.EqTo

/** A Mockito [[ArgumentMatcher]] for comparing two `Option`s, using a given nested matcher when the `Option` exists */
class OptionMatcher[T](expectedOption: Option[T],
                       valMatcher: T => ArgumentMatcher[T] = (value: T) => EqTo[T](value)) extends ArgumentMatcher[Option[T]] {

  override def matches(actualOption: Option[T]): Boolean = {
    (actualOption, expectedOption) match {
      case (Some(actualVal), Some(expectedVal)) => valMatcher(expectedVal).matches(actualVal)
      case (None, None) => true
      case _ => false
    }
  }

  override def toString: String = s"Matcher for: $expectedOption"
}

object OptionMatcher {

  /** A convenience reporter method (using `argThat`) for [[OptionMatcher]] */
  def eqOption[T](expectedOption: Option[T],
                  valMatcherGenerator: T => ArgumentMatcher[T] = (value: T) => EqTo[T](value)): Option[T] =
    argThat(new OptionMatcher[T](expectedOption, valMatcherGenerator))
}



