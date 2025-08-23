package io.github.effiban.scala2java.test.utils.matchers

import org.mockito.ArgumentMatcher
import org.mockito.ArgumentMatchers.{argThat, intThat}
import org.mockito.matchers.EqTo

/** A Mockito [[ArgumentMatcher]] for comparing two multi-lists of elements, using a given element matcher */
class MultiListMatcher[T](expectedMultiList: List[List[T]],
                          elemMatcher: T => ArgumentMatcher[T] = (elem: T) => EqTo[T](elem)) extends ArgumentMatcher[List[List[T]]] {

  override def matches(actualMultiList: List[List[T]]): Boolean = {
    actualMultiList.size == expectedMultiList.size &&
      actualMultiList.zipWithIndex.forall {
        case (actualElem, idx) => new ListMatcher(expectedMultiList(idx), elemMatcher).matches(actualMultiList(idx))
      }
  }

  override def toString: String = s"Matcher for: $expectedMultiList"
}

object MultiListMatcher {
  /** A convenience reporter method (using `argThat`) for [[MultiListMatcher]] */
  def eqMultiList[T](expected: List[List[T]],
                elemMatcher: T => ArgumentMatcher[T] = (value: T) => EqTo[T](value)): List[List[T]] =
    argThat(new MultiListMatcher(expected, elemMatcher))
}


