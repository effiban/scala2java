package io.github.effiban.scala2java.core.desugarers.syntactic

import io.github.effiban.scala2java.core.desugarers.syntactic.TermApplyInfixToTupleDesugarer.desugar
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite

import scala.meta.XtensionQuasiquoteTerm

class TermApplyInfixToTupleDesugarerTest extends UnitTestSuite {

  test("desugar") {
    desugar(q"a -> 1").structure shouldBe q"(a, 1)".structure
  }
}
