package io.github.effiban.scala2java.core.predicates

import io.github.effiban.scala2java.core.classifiers.TermNameClassifier
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.XtensionQuasiquoteTerm

class CoreTermSelectSupportsNoArgInvocationTest extends UnitTestSuite {

  private val termNameClassifier = mock[TermNameClassifier]

  private val coreTermSelectSupportsNoArgInvocation = new CoreTermSelectSupportsNoArgInvocation(termNameClassifier)

  test("apply() when name is 'apply' and qualifier has apply() method should return true") {
    val termSelect = q"x.apply"
    val qual = q"x"

    when(termNameClassifier.hasApplyMethod(eqTree(qual))).thenReturn(true)

    coreTermSelectSupportsNoArgInvocation(termSelect) shouldBe true
  }

  test("apply() when name is 'apply' and qualifier does not have apply() method should return false") {
    val termSelect = q"x.apply"
    val qual = q"x"

    when(termNameClassifier.hasApplyMethod(eqTree(qual))).thenReturn(false)

    coreTermSelectSupportsNoArgInvocation(termSelect) shouldBe false
  }

  test("apply() when name is 'empty' and qualifier has empty() method should return true") {
    val termSelect = q"x.empty"
    val qual = q"x"

    when(termNameClassifier.hasEmptyMethod(eqTree(qual))).thenReturn(true)

    coreTermSelectSupportsNoArgInvocation(termSelect) shouldBe true
  }

  test("apply() when name is 'apply' and qualifier does not have empty() method should return false") {
    val termSelect = q"x.empty"
    val qual = q"x"

    when(termNameClassifier.hasEmptyMethod(eqTree(qual))).thenReturn(false)

    coreTermSelectSupportsNoArgInvocation(termSelect) shouldBe false
  }

  test("apply() when name is 'toString' should return true") {
    val termSelect = q"x.toString"

    coreTermSelectSupportsNoArgInvocation(termSelect) shouldBe true
  }

  test("apply() when name is unrecognized should return false") {
    val termSelect = q"x.blabla"

    coreTermSelectSupportsNoArgInvocation(termSelect) shouldBe false
  }
}
