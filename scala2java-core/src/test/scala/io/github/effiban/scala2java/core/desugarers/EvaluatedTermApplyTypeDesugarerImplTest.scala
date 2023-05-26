package io.github.effiban.scala2java.core.desugarers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite

import scala.meta.XtensionQuasiquoteTerm

class EvaluatedTermApplyTypeDesugarerImplTest extends UnitTestSuite {

  private val evaluatedTermApplyTypeDesugarer = new EvaluatedTermApplyTypeDesugarerImpl()

  test("desugar classOf[T] should return unchanged") {
    val classOfT = q"classOf[T]"

    evaluatedTermApplyTypeDesugarer.desugar(classOfT).structure shouldBe classOfT.structure
  }

  test("desugar foo[T] should return foo[T]()") {
    val termApplyType = q"foo[T]"
    val termApply = q"foo[T]()"

    evaluatedTermApplyTypeDesugarer.desugar(termApplyType).structure shouldBe termApply.structure
  }
}
