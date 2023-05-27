package io.github.effiban.scala2java.core.desugarers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchersSugar.any

import scala.meta.{Term, XtensionQuasiquoteTerm}

class DefnDefDesugarerImplTest extends UnitTestSuite {

  private val termParamDesugarer = mock[TermParamDesugarer]
  private val evaluatedTermDesugarer = mock[EvaluatedTermDesugarer]

  private val defnDefDesugarer = new DefnDefDesugarerImpl(termParamDesugarer, evaluatedTermDesugarer)


  test("desugar") {
    val defnDef =
      q"""
      def foo(x: Int = func, y: String = func2)(z: Int = func3) {
         func4
      }
      """

    val desugaredDefnDef =
      q"""
      def foo(x: Int = func(), y: String = func2())(z: Int = func3()) {
        func4()
      }
      """

    doAnswer((termParam: Term.Param) => termParam.default match {
      case Some(q"func") => termParam.copy(default = Some(q"func()"))
      case Some(q"func2") => termParam.copy(default = Some(q"func2()"))
      case Some(q"func3") => termParam.copy(default = Some(q"func3()"))
      case _ => termParam
    }).when(termParamDesugarer).desugar(any[Term.Param])

    doReturn(desugaredDefnDef.body).when(evaluatedTermDesugarer).desugar(eqTree(defnDef.body))

    defnDefDesugarer.desugar(defnDef).structure shouldBe desugaredDefnDef.structure
  }

}
