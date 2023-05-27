package io.github.effiban.scala2java.core.desugarers.semantic

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.{XtensionQuasiquoteTerm, XtensionQuasiquoteTermParam}

class TermParamDesugarerImplTest extends UnitTestSuite {

  private val evaluatedTermDesugarer = mock[EvaluatedTermDesugarer]

  private val termParamDesugarer = new TermParamDesugarerImpl(evaluatedTermDesugarer)

  test("desugar when has no default") {
    val termParam = param"x: Int"
    termParamDesugarer.desugar(termParam).structure shouldBe termParam.structure
  }

  test("desugar when has default") {
    val termParam = param"x: Int = func"
    val desugaredTermParam = param"x: Int = func()"

    val default = q"func"
    val desugaredDefault = q"func()"

    doReturn(desugaredDefault).when(evaluatedTermDesugarer).desugar(eqTree(default))

    termParamDesugarer.desugar(termParam).structure shouldBe desugaredTermParam.structure
  }
}
