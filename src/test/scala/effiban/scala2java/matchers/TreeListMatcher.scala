package effiban.scala2java.matchers

import org.mockito.ArgumentMatcher
import org.mockito.ArgumentMatchers.argThat

import scala.meta.Tree

class TreeListMatcher[T <: Tree](expected: List[T]) extends ArgumentMatcher[List[T]] {

  override def matches(actual: List[T]): Boolean = {
    actual.structure == expected.structure
  }

  override def toString: String = s"Matcher for: $expected"
}

object TreeListMatcher {
  def eqTreeList[T <: Tree](expected: List[T]): List[T] = argThat(new TreeListMatcher(expected))
}


