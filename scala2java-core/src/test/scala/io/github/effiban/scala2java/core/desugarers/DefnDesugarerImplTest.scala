package io.github.effiban.scala2java.core.desugarers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import org.mockito.ArgumentMatchers.any

import scala.meta.{Tree, XtensionQuasiquoteTerm}

class DefnDesugarerImplTest extends UnitTestSuite {

  private val treeDesugarer = mock[TreeDesugarer]

  private val defnDesugarer = new DefnDesugarerImpl(treeDesugarer)

  test("desugar Defn.Val") {
    val defnVal = q"val x: Int = 3"

    when(treeDesugarer.desugar(any())).thenAnswer((t: Tree) => t)

    defnDesugarer.desugar(defnVal).structure shouldBe defnVal.structure

    verify(treeDesugarer, times(3)).desugar(any())
  }

}
