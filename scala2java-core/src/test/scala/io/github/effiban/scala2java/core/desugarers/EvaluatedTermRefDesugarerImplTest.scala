package io.github.effiban.scala2java.core.desugarers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite

import scala.meta.XtensionQuasiquoteTerm

class EvaluatedTermRefDesugarerImplTest extends UnitTestSuite {

  private val treeDesugarer = mock[TreeDesugarer]

  private val evaluatedTermRefDesugarer = new EvaluatedTermRefDesugarerImpl(treeDesugarer)

  test("desugar Apply.Unary") {
    val applyUnary = q"!func"

    // TODO modify once EvaluatedTermNameDesugarer is implemented
    evaluatedTermRefDesugarer.desugar(applyUnary).structure shouldBe applyUnary.structure

  }

}
