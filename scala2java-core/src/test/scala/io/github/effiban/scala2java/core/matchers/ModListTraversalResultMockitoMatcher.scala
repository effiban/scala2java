package io.github.effiban.scala2java.core.matchers

import io.github.effiban.scala2java.core.traversers.results.ModListTraversalResult
import org.mockito.ArgumentMatcher
import org.mockito.ArgumentMatchers.argThat

class ModListTraversalResultMockitoMatcher(expectedTraversalResult: ModListTraversalResult)
  extends ArgumentMatcher[ModListTraversalResult] {

  override def matches(actualTraversalResult: ModListTraversalResult): Boolean = {
    scalaModsMatch(actualTraversalResult) && javaModifiersMatch(actualTraversalResult)
  }

  override def toString: String = s"Matcher for: $expectedTraversalResult"

  private def scalaModsMatch(actualTraversalResult: ModListTraversalResult) = {
    actualTraversalResult.scalaMods.structure == expectedTraversalResult.scalaMods.structure
  }

  private def javaModifiersMatch(actualTraversalResult: ModListTraversalResult): Boolean = {
    actualTraversalResult.javaModifiers == expectedTraversalResult.javaModifiers
  }

}

object ModListTraversalResultMockitoMatcher {
  def eqModListTraversalResult(expectedTraversalResult: ModListTraversalResult): ModListTraversalResult =
    argThat(new ModListTraversalResultMockitoMatcher(expectedTraversalResult))
}

