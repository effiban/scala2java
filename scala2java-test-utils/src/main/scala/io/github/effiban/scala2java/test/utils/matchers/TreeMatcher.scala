package io.github.effiban.scala2java.test.utils.matchers

import org.mockito.ArgumentMatcher
import org.mockito.ArgumentMatchers.argThat

import scala.meta.Tree

/** A Mockito [[ArgumentMatcher]] for comparing two [[scala.meta.Tree]]-s by comparing their structure members */
class TreeMatcher[T <: Tree](expected: T) extends ArgumentMatcher[T] {

  override def matches(actual: T): Boolean = {
    actual.structure == expected.structure
  }

  override def toString: String = s"Matcher for: $expected"
}

object TreeMatcher {
  /** A convenience reporter method (using `argThat`) for [[TreeMatcher]] */
  def eqTree[T <: Tree](expected: T): T = argThat(new TreeMatcher(expected))
}
