package io.github.effiban.scala2java.core.desugarers.syntactic

import io.github.effiban.scala2java.core.desugarers.syntactic.DefnValToDefnVarDesugarer.desugar
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite

import scala.meta.XtensionQuasiquoteTerm

class DefnValToDefnVarDesugarerTest extends UnitTestSuite {

  test("desugar when typed") {
    val defnVal = q"private val x: Int = 3"
    val expectedDefnVar = q"private final var x: Int = 3"

    desugar(defnVal).structure shouldBe expectedDefnVar.structure
  }

  test("desugar when untyped") {
    val defnVal = q"private val x = 3"
    val expectedDefnVar = q"private final var x = 3"

    desugar(defnVal).structure shouldBe expectedDefnVar.structure
  }
}
