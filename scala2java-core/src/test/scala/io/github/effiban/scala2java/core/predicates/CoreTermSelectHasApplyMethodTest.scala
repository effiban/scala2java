package io.github.effiban.scala2java.core.predicates

import io.github.effiban.scala2java.core.classifiers.TermSelectClassifier
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.XtensionQuasiquoteTerm

class CoreTermSelectHasApplyMethodTest extends UnitTestSuite {

  private val termSelectClassifier = mock[TermSelectClassifier]

  private val coreTermSelectHasApplyMethod = new CoreTermSelectHasApplyMethod(termSelectClassifier)

  test("apply() when has apply method") {
    val termSelect = q"x.y"

    when(termSelectClassifier.hasApplyMethod(eqTree(termSelect))).thenReturn(true)

    coreTermSelectHasApplyMethod(termSelect) shouldBe true
  }

  test("apply() when has no apply method") {
    val termSelect = q"x.y"

    when(termSelectClassifier.hasApplyMethod(eqTree(termSelect))).thenReturn(false)

    coreTermSelectHasApplyMethod(termSelect) shouldBe false
  }

}
