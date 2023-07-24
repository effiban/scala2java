package io.github.effiban.scala2java.test.utils.matchers

import org.mockito.ArgumentMatcher
import org.mockito.matchers.EqTo

import scala.meta.Tree

class TreeKeyedMapMockitoMatcher[K <: Tree, V](expectedMap: Map[K, V],
                                               valueMatcher: V => ArgumentMatcher[V] = (elem: V) => EqTo[V](elem))
  extends ArgumentMatcher[Map[K, V]] {

  override def matches(actualMap: Map[K, V]): Boolean = {
    sizeMatches(actualMap) && contentsMatch(actualMap)
  }

  override def toString: String = s"Matcher for: $expectedMap"

  private def sizeMatches(actualMap: Map[K, V]) = {
    actualMap.size == expectedMap.size
  }

  private def contentsMatch(actualMap: Map[K, V]): Boolean = {
    actualMap.forall { case (actualKey, actualValue) =>
      expectedMap.exists { case (expectedKey, expectedValue) =>
        actualKey.structure == expectedKey.structure && valueMatcher(expectedValue).matches(actualValue)
      }
    }
  }
}
