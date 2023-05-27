package io.github.effiban.scala2java.core.desugarers.semantic

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.spi.predicates.TermNameSupportsNoArgInvocation
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.XtensionQuasiquoteTerm

class EvaluatedTermNameDesugarerTest extends UnitTestSuite {

  private val termNameSupportsNoArgInvocation = mock[TermNameSupportsNoArgInvocation]

  private val termNameDesugarer = new EvaluatedTermNameDesugarerImpl(termNameSupportsNoArgInvocation)


  test("transform() when supports no-arg invocation - should return a Term.Apply") {
    val termName = q"foo"
    val expectedTermApply = q"foo()"

    when(termNameSupportsNoArgInvocation(eqTree(termName))).thenReturn(true)

    termNameDesugarer.desugar(termName).structure shouldBe expectedTermApply.structure
  }

  test("transform() when does not support no-arg invocation - should return unchanged") {
    val termName = q"foo"

    when(termNameSupportsNoArgInvocation(eqTree(termName))).thenReturn(false)

    termNameDesugarer.desugar(termName).structure shouldBe termName.structure
  }

}
