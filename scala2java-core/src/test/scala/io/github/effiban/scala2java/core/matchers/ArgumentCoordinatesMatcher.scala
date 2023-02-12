package io.github.effiban.scala2java.core.matchers

import io.github.effiban.scala2java.core.entities.ArgumentCoordinates
import io.github.effiban.scala2java.test.utils.matchers.{OptionMatcher, TreeMatcher}
import org.mockito.ArgumentMatcher
import org.mockito.ArgumentMatchers.argThat

import scala.meta.{Term, Tree}

class ArgumentCoordinatesMatcher(expectedCoords: ArgumentCoordinates) extends ArgumentMatcher[ArgumentCoordinates] {

  override def matches(actualCoords: ArgumentCoordinates): Boolean = {
    parentsMatch(actualCoords) &&
      maybeNamesMatch(actualCoords) &&
      indexesMatch(actualCoords)
  }

  private def parentsMatch(actualCoords: ArgumentCoordinates) = {
    new TreeMatcher[Tree](expectedCoords.parent).matches(actualCoords.parent)
  }

  private def maybeNamesMatch(actualCoords: ArgumentCoordinates) = {
    new OptionMatcher[Term.Name](expectedCoords.maybeName, new TreeMatcher[Term.Name](_)).matches(actualCoords.maybeName)
  }

  private def indexesMatch(actualCoords: ArgumentCoordinates): Boolean = actualCoords.index == expectedCoords.index

  override def toString: String = s"Matcher for: $expectedCoords"
}

object ArgumentCoordinatesMatcher {
  def eqArgumentCoordinates(expectedCoords: ArgumentCoordinates): ArgumentCoordinates =
    argThat(new ArgumentCoordinatesMatcher(expectedCoords))
}

