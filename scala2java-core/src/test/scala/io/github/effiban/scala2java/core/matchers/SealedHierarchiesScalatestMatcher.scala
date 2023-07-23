package io.github.effiban.scala2java.core.matchers

import io.github.effiban.scala2java.core.entities.SealedHierarchies
import org.scalatest.matchers.{MatchResult, Matcher}

class SealedHierarchiesScalatestMatcher(expectedSealedHierarchies: SealedHierarchies) extends Matcher[SealedHierarchies] {

  override def apply(actualSealedHierarchies: SealedHierarchies): MatchResult = {
    val matches = actualSealedHierarchies.asStringMap() == expectedSealedHierarchies.asStringMap()

    MatchResult(matches,
      s"Actual sealed hierarchies: $actualSealedHierarchies is NOT the same as expected sealed hierarchies: $expectedSealedHierarchies",
      s"Actual sealed hierarchies: $actualSealedHierarchies the same as expected sealed hierarchies: $expectedSealedHierarchies"
    )
  }

  override def toString: String = s"Matcher for: $expectedSealedHierarchies"
}

