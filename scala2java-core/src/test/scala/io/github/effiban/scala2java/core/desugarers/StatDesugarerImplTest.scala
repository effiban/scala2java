package io.github.effiban.scala2java.core.desugarers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.XtensionQuasiquoteTerm

class StatDesugarerImplTest extends UnitTestSuite {

  private val defnDesugarer = mock[DefnDesugarer]

  private val statDesugarer = new StatDesugarerImpl(defnDesugarer)

  test("desugar Defn") {
    val defn = q"val x = calc"
    val desugaredDefn = q"val x = calc()"

    doReturn(desugaredDefn).when(defnDesugarer).desugar(eqTree(defn))

    statDesugarer.desugar(defn).structure shouldBe desugaredDefn.structure
  }

  test("desugar Import") {
    val `import` = q"import a.b.c"

    statDesugarer.desugar(`import`).structure shouldBe `import`.structure
  }
}
