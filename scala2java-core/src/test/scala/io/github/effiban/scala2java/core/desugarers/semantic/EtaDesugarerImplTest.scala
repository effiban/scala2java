package io.github.effiban.scala2java.core.desugarers.semantic

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.XtensionQuasiquoteTerm

class EtaDesugarerImplTest extends UnitTestSuite {

  private val evaluatedTermSelectQualDesugarer = mock[EvaluatedTermSelectQualDesugarer]
  private val termApplyTypeFunDesugarer = mock[TermApplyTypeFunDesugarer]
  private val evaluatedTermDesugarer = mock[EvaluatedTermDesugarer]

  private val etaDesugarer = new EtaDesugarerImpl(
    evaluatedTermSelectQualDesugarer,
    termApplyTypeFunDesugarer,
    evaluatedTermDesugarer
  )

  test("desugar when expr is a Term.Name should remain unchanged") {
    val eta = q"func _"
    etaDesugarer.desugar(eta).structure shouldBe eta.structure
  }

  test("desugar when expr is a Term.Select should desugar the qualifier") {
    val termSelect = q"(func(func2)).myMethod"
    val desugaredTermSelect = q"(func(func2())).myMethod"

    val eta = q"(func(func2)).myMethod _"
    val desugaredEta = q"(func(func2())).myMethod _"

    doReturn(desugaredTermSelect).when(evaluatedTermSelectQualDesugarer).desugar(eqTree(termSelect))

    etaDesugarer.desugar(eta).structure shouldBe desugaredEta.structure
  }

  test("desugar() when expr is a Term.ApplyType, should desugar the ApplyType 'fun' part") {
    val termApplyType = q"(func(func2)).myMethod[Int]"
    val desugaredTermApplyType = q"(func(func2())).myMethod[Int]"

    val eta = q"(func(func2)).myMethod[Int] _"
    val desugaredEta = q"(func(func2())).myMethod[Int] _"

    doReturn(desugaredTermApplyType).when(termApplyTypeFunDesugarer).desugar(eqTree(termApplyType))

    etaDesugarer.desugar(eta).structure shouldBe desugaredEta.structure
  }

  test("desugar() when expr is a 'If' should desugar it") {
    val fun = q"if (flag) then func1 else func2"
    val desugaredFun = q"if (flag) then func1() else func2()"

    val eta = q"(if (flag) then func1 else func2) _"
    val desugaredEta = q"(if (flag) then func1() else func2()) _"

    doReturn(desugaredFun).when(evaluatedTermDesugarer).desugar(eqTree(fun))

    etaDesugarer.desugar(eta).structure shouldBe desugaredEta.structure
  }

}
