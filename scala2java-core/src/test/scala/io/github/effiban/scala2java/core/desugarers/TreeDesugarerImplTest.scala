package io.github.effiban.scala2java.core.desugarers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.{Pkg, XtensionQuasiquoteTerm, XtensionQuasiquoteType}

class TreeDesugarerImplTest extends UnitTestSuite {

  private val statDesugarer = mock[StatDesugarer]

  private val treeDesugarer = new TreeDesugarerImpl(statDesugarer)

  test("desugar stat") {
    val stat = Pkg(q"a.b", Nil)
    val desugaredStat = Pkg(q"a.b.c", Nil)

    doReturn(desugaredStat).when(statDesugarer).desugar(eqTree(stat))

    treeDesugarer.desugar(stat).structure shouldBe desugaredStat.structure
  }

  test("desugar type") {
    val tpe = t"MyType"

    treeDesugarer.desugar(tpe).structure shouldBe tpe.structure
  }
}
