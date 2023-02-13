package io.github.effiban.scala2java.core.matchers

import io.github.effiban.scala2java.spi.entities.InvocationArgCoordinates
import io.github.effiban.scala2java.test.utils.matchers.{OptionMatcher, TreeMatcher}
import org.mockito.ArgumentMatcher
import org.mockito.ArgumentMatchers.argThat

import scala.meta.{Term, Tree}

class InvocationArgCoordinatesMatcher(expectedCoords: InvocationArgCoordinates) extends ArgumentMatcher[InvocationArgCoordinates] {

  override def matches(actualCoords: InvocationArgCoordinates): Boolean = {
    invocationsMatch(actualCoords) &&
      maybeNamesMatch(actualCoords) &&
      indexesMatch(actualCoords)
  }

  private def invocationsMatch(actualCoords: InvocationArgCoordinates) = {
    new TreeMatcher[Tree](expectedCoords.invocation).matches(actualCoords.invocation)
  }

  private def maybeNamesMatch(actualCoords: InvocationArgCoordinates) = {
    new OptionMatcher[Term.Name](expectedCoords.maybeName, new TreeMatcher[Term.Name](_)).matches(actualCoords.maybeName)
  }

  private def indexesMatch(actualCoords: InvocationArgCoordinates): Boolean = actualCoords.index == expectedCoords.index

  override def toString: String = s"Matcher for: $expectedCoords"
}

object InvocationArgCoordinatesMatcher {
  def eqArgumentCoordinates(expectedCoords: InvocationArgCoordinates): InvocationArgCoordinates =
    argThat(new InvocationArgCoordinatesMatcher(expectedCoords))
}

