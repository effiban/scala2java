package io.github.effiban.scala2java.core.desugarers.semantic

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchers.any

import scala.meta.{Term, XtensionQuasiquoteTerm}

class TermApplyDesugarerImplTest extends UnitTestSuite {

  private val termApplyFunDesugarer = mock[TermApplyFunDesugarer]
  private val evaluatedTermDesugarer = mock[EvaluatedTermDesugarer]

  private val termApplyDesugarer = new TermApplyDesugarerImpl(termApplyFunDesugarer, evaluatedTermDesugarer)

  test("desugar") {

    val termApply = q"MyObj(func1, func2)"
    val desugaredFunTermApply = q"MyObj.apply(func1, func2)"

    val arg1 = q"func1"
    val arg2 = q"func2"

    val desugaredArg1 = q"func1()"
    val desugaredArg2 = q"func2()"

    val desugaredTermApply = q"MyObj.apply(func1(), func2())"

    doReturn(desugaredFunTermApply).when(termApplyFunDesugarer).desugar(eqTree(termApply))
    doAnswer((arg: Term) => arg match {
      case anArg if anArg.structure == arg1.structure => desugaredArg1
      case anArg if anArg.structure == arg2.structure => desugaredArg2
      case anArg => anArg
    }).when(evaluatedTermDesugarer).desugar(any[Term])

    termApplyDesugarer.desugar(termApply).structure shouldBe desugaredTermApply.structure

  }

}
