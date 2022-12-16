package io.github.effiban.scala2java.test.utils.matchers

import org.mockito.ArgumentMatcher
import org.mockito.ArgumentMatchers.argThat
import org.mockito.matchers.EqTo

/** A Mockito [[ArgumentMatcher]] for comparing two lists of elements, using a given element matcher */
class ListMatcher[T](expectedElems: List[T],
                     elemMatcher: T => ArgumentMatcher[T] = (elem: T) => EqTo[T](elem)) extends ArgumentMatcher[List[T]] {

  override def matches(actualElems: List[T]): Boolean = {
    actualElems.size == expectedElems.size &&
      actualElems.zipWithIndex.forall {
        case (actualElem, idx) => elemMatcher(expectedElems(idx)).matches(actualElem)
      }
  }

  override def toString: String = s"Matcher for: $expectedElems"
}

object ListMatcher {
  /** A convenience reporter method (using `argThat`) for [[ListMatcher]] */
  def eqList[T](expected: List[T],
                elemMatcher: T => ArgumentMatcher[T] = (value: T) => EqTo[T](value)): List[T] = argThat(new ListMatcher(expected, elemMatcher))
}


