package io.github.effiban.scala2java.test.utils.matchers

import org.scalatest.matchers.{MatchResult, Matcher}

import scala.meta.Tree

class TreeKeyedMapScalatestMatcher[K <: Tree, V](expectedMap: Map[K, V], valueMatcher: V => Matcher[V])
  extends Matcher[Map[K, V]] {

  override def apply(actualMap: Map[K, V]): MatchResult = {
    val matches = sizeMatches(actualMap) && contentsMatch(actualMap)

    MatchResult(matches,
      s"Actual context: $actualMap is NOT the same as expected context: $expectedMap",
      s"Actual context: $actualMap the same as expected context: $expectedMap"
    )
  }

  override def toString: String = s"Matcher for: $expectedMap"

  private def sizeMatches(actualMap: Map[K, V]) = {
    actualMap.size == expectedMap.size
  }

  private def contentsMatch(actualMap: Map[K, V]): Boolean = {
    actualMap.forall { case (actualKey, actualValue) =>
      expectedMap.exists { case (expectedKey, expectedValue) =>
        actualKey.structure == expectedKey.structure && valueMatcher(expectedValue)(actualValue).matches
      }
    }
  }
}
