package io.github.effiban.scala2java.core.desugarers.semantic

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.XtensionQuasiquoteTerm

class AssignDesugarerImplTest extends UnitTestSuite {

  private val evaluatedTermDesugarer = mock[EvaluatedTermDesugarer]

  private val assignDesugarer = new AssignDesugarerImpl(evaluatedTermDesugarer)

  test("desugar") {
    val assign = q"x = func"
    val desugaredAssign = q"x = func()"

    doReturn(q"func()").when(evaluatedTermDesugarer).desugar(eqTree(q"func"))

    assignDesugarer.desugar(assign).structure shouldBe desugaredAssign.structure
  }

}
