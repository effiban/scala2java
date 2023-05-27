package io.github.effiban.scala2java.core.desugarers.semantic

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import org.mockito.ArgumentMatchers.any

import scala.meta.{Term, XtensionQuasiquoteTerm}

class TermApplyInfixDesugarerImplTest extends UnitTestSuite {

  private val evaluatedTermDesugarer = mock[EvaluatedTermDesugarer]

  private val termApplyInfixDesugarer = new TermApplyInfixDesugarerImpl(evaluatedTermDesugarer)

  test("testDesugar") {
    val termApplyInfix = q"arg1 op(arg2, arg3)"
    val desugaredTermApplyInfix = q"arg1() op(arg2(), arg3())"

    doAnswer((arg: Term) => arg match {
      case anArg if anArg.structure == q"arg1".structure => q"arg1()"
      case anArg if anArg.structure == q"arg2".structure => q"arg2()"
      case anArg if anArg.structure == q"arg3".structure => q"arg3()"
      case anArg => anArg
    }).when(evaluatedTermDesugarer).desugar(any[Term])

    termApplyInfixDesugarer.desugar(termApplyInfix).structure shouldBe desugaredTermApplyInfix.structure
  }

}
