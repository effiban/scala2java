package effiban.scala2java.matchers

import org.mockito.ArgumentMatcher
import org.mockito.ArgumentMatchers.argThat
import org.mockito.matchers.EqTo

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

  def eqSome[T](expectedVal: T,
                valMatcherGenerator: T => ArgumentMatcher[T] = (value: T) => EqTo[T](value)): Option[T] =
    argThat(new SomeMatcher[T](expectedVal, valMatcherGenerator))
}



