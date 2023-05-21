package io.github.effiban.scala2java.core.desugarers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.{XtensionQuasiquoteCaseOrPattern, XtensionQuasiquoteTerm, XtensionQuasiquoteTermParam, XtensionQuasiquoteType}

class TreeDesugarerImplTest extends UnitTestSuite {

  private val statDesugarer = mock[StatDesugarer]
  private val termParamDesugarer = mock[TermParamDesugarer]

  private val treeDesugarer = new TreeDesugarerImpl(statDesugarer, termParamDesugarer)

  test("desugar Stat") {
    val stat = q"def foo() = { func }"
    val desugaredStat = q"def foo() = { func() }"

    doReturn(desugaredStat).when(statDesugarer).desugar(eqTree(stat))

    treeDesugarer.desugar(stat).structure shouldBe desugaredStat.structure
  }

  test("desugar Term.Param") {
    val termParam = param"x: Int = func"
    val desugaredTermParam = param"x: Int = func()"

    doReturn(desugaredTermParam).when(termParamDesugarer).desugar(eqTree(termParam))

    treeDesugarer.desugar(termParam).structure shouldBe desugaredTermParam.structure
  }

  test("desugar Pat") {
    val pat = p"x"

    treeDesugarer.desugar(pat).structure shouldBe pat.structure
  }

  test("desugar Type") {
    val tpe = t"MyType"

    treeDesugarer.desugar(tpe).structure shouldBe tpe.structure
  }
}
