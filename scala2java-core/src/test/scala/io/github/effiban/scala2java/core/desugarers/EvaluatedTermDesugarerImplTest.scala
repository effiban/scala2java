package io.github.effiban.scala2java.core.desugarers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.{XtensionQuasiquoteInit, XtensionQuasiquoteTerm}

class EvaluatedTermDesugarerImplTest extends UnitTestSuite {

  private val evaluatedTermRefDesugarer = mock[EvaluatedTermRefDesugarer]
  private val termApplyTypeDesugarer = mock[TermApplyTypeDesugarer]
  private val treeDesugarer = mock[TreeDesugarer]

  private val evaluatedTermDesugarer = new EvaluatedTermDesugarerImpl(
    evaluatedTermRefDesugarer,
    termApplyTypeDesugarer,
    treeDesugarer
  )

  test("desugar Term.Name") {
    val termName = q"func"
    val termApply = q"func()"

    doReturn(termApply).when(evaluatedTermRefDesugarer).desugar(eqTree(termName))

    evaluatedTermDesugarer.desugar(termName).structure shouldBe termApply.structure
  }

  test("desugar Term.ApplyType") {
    val termApplyType = q"func[Int]"
    val termApply = q"func[Int]()"

    doReturn(termApply).when(termApplyTypeDesugarer).desugar(eqTree(termApplyType))

    evaluatedTermDesugarer.desugar(termApplyType).structure shouldBe termApply.structure
  }

  test("desugar New") {
    val `new` = q"new A(func)"
    val desugaredNew = q"new A(func())"

    val init = init"A(func)"
    val desugaredInit = init"A(func())"

    doReturn(desugaredInit).when(treeDesugarer).desugar(eqTree(init))

    evaluatedTermDesugarer.desugar(`new`).structure shouldBe desugaredNew.structure
  }

  test("desugar Return(Lit)") {
    val `return` = q"return 3"

    evaluatedTermDesugarer.desugar(`return`).structure shouldBe `return`.structure
  }

  test("desugar Return(Term.Name)") {
    val termName = q"func"
    val termApply = q"func()"

    val `return` = q"return func"
    val desugaredReturn = q"return func()"

    doReturn(termApply).when(evaluatedTermRefDesugarer).desugar(eqTree(termName))

    evaluatedTermDesugarer.desugar(`return`).structure shouldBe desugaredReturn.structure
  }
}
