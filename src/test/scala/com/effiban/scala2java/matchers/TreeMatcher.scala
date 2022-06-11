package com.effiban.scala2java.matchers

import org.mockito.ArgumentMatcher
import org.mockito.ArgumentMatchers.argThat

import scala.meta.Tree

class TreeMatcher[T <: Tree](expected: T) extends ArgumentMatcher[T] {

  override def matches(actual: T): Boolean = {
    actual.structure == expected.structure
  }
}

object TreeMatcher {
  def eqTree[T <: Tree](expected: T): T = argThat(new TreeMatcher(expected))
}
