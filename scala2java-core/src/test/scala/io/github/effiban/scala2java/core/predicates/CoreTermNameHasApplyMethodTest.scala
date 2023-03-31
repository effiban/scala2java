package io.github.effiban.scala2java.core.predicates

import io.github.effiban.scala2java.core.classifiers.TermNameClassifier
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.XtensionQuasiquoteTerm

class CoreTermNameHasApplyMethodTest extends UnitTestSuite {

  private val termNameClassifier = mock[TermNameClassifier]

  private val coreTermNameHasApplyMethod = new CoreTermNameHasApplyMethod(termNameClassifier)

  test("apply() when has apply method") {
    val termName = q"x"

    when(termNameClassifier.hasApplyMethod(eqTree(termName))).thenReturn(true)

    coreTermNameHasApplyMethod(termName) shouldBe true
  }

  test("apply() when has no apply method") {
    val termName = q"x"

    when(termNameClassifier.hasApplyMethod(eqTree(termName))).thenReturn(false)

    coreTermNameHasApplyMethod(termName) shouldBe false
  }
}
