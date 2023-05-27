package io.github.effiban.scala2java.core.desugarers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.XtensionQuasiquoteTerm

class TermApplyTypeDesugarerImplTest extends UnitTestSuite {

  private val termApplyTypeFunDesugarer = mock[TermApplyTypeFunDesugarer]

  private val termApplyTypeDesugarer = new TermApplyTypeDesugarerImpl(termApplyTypeFunDesugarer)

  test("desugar classOf[T] should return unchanged") {
    val classOfT = q"classOf[T]"

    termApplyTypeDesugarer.desugar(classOfT).structure shouldBe classOfT.structure
  }

  test("desugar regular Term.ApplyType should return a Term.Apply with the 'fun' part desugared") {
    val termApplyType = q"func(func2)[Int]"
    val desugaredFunApplyType = q"func(func2())[Int]"
    val termApply = q"func(func2())[Int]()"

    doReturn(desugaredFunApplyType).when(termApplyTypeFunDesugarer).desugar(eqTree(termApplyType))

    termApplyTypeDesugarer.desugar(termApplyType).structure shouldBe termApply.structure
  }
}
