package io.github.effiban.scala2java.core.desugarers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite

import scala.meta.XtensionQuasiquoteTerm

class TermApplyTypeDesugarerImplTest extends UnitTestSuite {

  private val termApplyTypeDesugarer = new TermApplyTypeDesugarerImpl()

  test("desugar classOf[T] should return unchanged") {
    val classOfT = q"classOf[T]"

    termApplyTypeDesugarer.desugar(classOfT).structure shouldBe classOfT.structure
  }

  test("desugar foo[T] should return foo[T]()") {
    val termApplyType = q"foo[T]"
    val termApply = q"foo[T]()"

    termApplyTypeDesugarer.desugar(termApplyType).structure shouldBe termApply.structure
  }
}
