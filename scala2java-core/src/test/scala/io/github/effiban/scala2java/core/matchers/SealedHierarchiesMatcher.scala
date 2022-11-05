package io.github.effiban.scala2java.core.matchers

import io.github.effiban.scala2java.core.entities.SealedHierarchies
import org.mockito.ArgumentMatcher
import org.mockito.ArgumentMatchers.argThat

class SealedHierarchiesMatcher(expectedSealedHierarchies: SealedHierarchies) extends ArgumentMatcher[SealedHierarchies] {

  override def matches(actualSealedHierarchies: SealedHierarchies): Boolean = {
    actualSealedHierarchies.asStringMap() == expectedSealedHierarchies.asStringMap()
  }

  override def toString: String = s"Matcher for: $expectedSealedHierarchies"
}

object SealedHierarchiesMatcher {
  def eqSealedHierarchies(expectedSealedHierarchies: SealedHierarchies): SealedHierarchies =
    argThat(new SealedHierarchiesMatcher(expectedSealedHierarchies))
}

