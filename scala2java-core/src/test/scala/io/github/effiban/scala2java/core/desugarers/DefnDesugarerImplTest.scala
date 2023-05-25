package io.github.effiban.scala2java.core.desugarers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchers.any

import scala.meta.{Tree, XtensionQuasiquoteTerm}

class DefnDesugarerImplTest extends UnitTestSuite {

  private val defnDefDesugarer = mock[DefnDefDesugarer]
  private val treeDesugarer = mock[TreeDesugarer]

  private val defnDesugarer = new DefnDesugarerImpl(defnDefDesugarer, treeDesugarer)

  test("desugar Defn.Def") {
    val defnDef =
      q"""
     def foo(x: Int = func) {
        func2
     }
     """

    val desugaredDefnDef =
      q"""
     def foo(x: Int = func()) {
       func2()
     }
     """

    doReturn(desugaredDefnDef).when(defnDefDesugarer).desugar(eqTree(defnDef))

    defnDesugarer.desugar(defnDef).structure shouldBe desugaredDefnDef.structure

  }

  test("desugar Defn.Val") {
    val defnVal = q"val x: Int = 3"

    when(treeDesugarer.desugar(any())).thenAnswer((t: Tree) => t)

    defnDesugarer.desugar(defnVal).structure shouldBe defnVal.structure

    verify(treeDesugarer, times(3)).desugar(any())
  }

}
