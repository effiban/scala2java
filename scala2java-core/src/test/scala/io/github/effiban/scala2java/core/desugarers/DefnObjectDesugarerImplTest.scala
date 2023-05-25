package io.github.effiban.scala2java.core.desugarers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.{Template, XtensionQuasiquoteTerm}

class DefnObjectDesugarerImplTest extends UnitTestSuite {

  private val templateDesugarer = mock[SameTypeDesugarer[Template]]

  private val defnObjectDesugarer = new DefnObjectDesugarerImpl(templateDesugarer)

  test("desugar") {
    val defnObject =
      q"""
      object myObj {
        val x: Int = func
      }
      """

    val desugaredDefnObject =
      q"""
      object myObj {
          val x: Int = func()
      }
      """

    doReturn(desugaredDefnObject.templ).when(templateDesugarer).desugar(eqTree(defnObject.templ))

    defnObjectDesugarer.desugar(defnObject).structure shouldBe desugaredDefnObject.structure

  }

}
