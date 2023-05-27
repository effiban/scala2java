package io.github.effiban.scala2java.core.desugarers.semantic

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.XtensionQuasiquoteTerm

class TermApplyTypeFunDesugarerImplTest extends UnitTestSuite {

  private val evaluatedTermSelectQualDesugarer = mock[EvaluatedTermSelectQualDesugarer]
  private val evaluatedTermDesugarer = mock[EvaluatedTermDesugarer]

  private val termApplyTypeFunDesugarer = new TermApplyTypeFunDesugarerImpl(evaluatedTermSelectQualDesugarer, evaluatedTermDesugarer)

  test("desugar when fun is a Term.Name should return unchanged") {
    val termApplyType = q"foo[Int]"
    termApplyTypeFunDesugarer.desugar(termApplyType).structure shouldBe termApplyType.structure
  }

  test("desugar when fun is a Term.Select should desugar its qualifier") {
    val termSelect = q"(func(func2)).x"
    val desugaredTermSelect = q"(func(func2())).x"

    val termApplyType = q"(func(func2)).x[Int]"
    val desugaredTermApplyType = q"(func(func2())).x[Int]"

    doReturn(desugaredTermSelect).when(evaluatedTermSelectQualDesugarer).desugar(eqTree(termSelect))

    termApplyTypeFunDesugarer.desugar(termApplyType).structure shouldBe desugaredTermApplyType.structure
  }

  test("desugar when fun is a Term.Apply should desugar it") {
    val fun = q"func(func2)"
    val desugaredFun = q"func(func2())"

    val termApplyType = q"func(func2)[Int]"
    val desugaredTermApplyType = q"func(func2())[Int]"

    doReturn(desugaredFun).when(evaluatedTermDesugarer).desugar(eqTree(fun))

    termApplyTypeFunDesugarer.desugar(termApplyType).structure shouldBe desugaredTermApplyType.structure
  }
}
