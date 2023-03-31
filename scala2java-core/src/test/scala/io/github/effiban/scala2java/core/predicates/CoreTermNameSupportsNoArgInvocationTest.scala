package io.github.effiban.scala2java.core.predicates

import io.github.effiban.scala2java.core.classifiers.TermNameClassifier
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.XtensionQuasiquoteTerm

class CoreTermNameSupportsNoArgInvocationTest extends UnitTestSuite {

  private val termNameClassifier = mock[TermNameClassifier]

  private val coreTermNameSupportsNoArgInvocation = new CoreTermNameSupportsNoArgInvocation(termNameClassifier)

  test("apply() when supports no-arg invocation") {
    val termName = q"x"

    when(termNameClassifier.supportsNoArgInvocation(eqTree(termName))).thenReturn(true)

    coreTermNameSupportsNoArgInvocation(termName) shouldBe true
  }

  test("apply() when doesn't support no-arg invocation") {
    val termName = q"x"

    when(termNameClassifier.supportsNoArgInvocation(eqTree(termName))).thenReturn(false)

    coreTermNameSupportsNoArgInvocation(termName) shouldBe false
  }
}
