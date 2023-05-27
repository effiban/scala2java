package io.github.effiban.scala2java.core.desugarers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.XtensionQuasiquoteTerm

class TermApplyTypeDesugarerImplTest extends UnitTestSuite {

  private val termApplyDesugarer = mock[TermApplyDesugarer]

  private val termApplyTypeDesugarer = new TermApplyTypeDesugarerImpl(termApplyDesugarer)

  test("desugar classOf[T] should return unchanged") {
    val classOfT = q"classOf[T]"

    termApplyTypeDesugarer.desugar(classOfT).structure shouldBe classOfT.structure
  }

  test("desugar regular Term.ApplyType should return a corresponding desugared Term.Apply") {
    val termApplyType = q"func(func2)[Int]"
    val termApply = q"func(func2)[Int]()"
    val desugaredTermApply = q"func(func2())[Int]()"

    doReturn(desugaredTermApply).when(termApplyDesugarer).desugar(eqTree(termApply))

    termApplyTypeDesugarer.desugar(termApplyType).structure shouldBe desugaredTermApply.structure
  }
}
