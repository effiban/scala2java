package io.github.effiban.scala2java.core.desugarers.syntactic

import io.github.effiban.scala2java.core.desugarers.syntactic.DeclValToDeclVarDesugarer.desugar
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite

import scala.meta.XtensionQuasiquoteTerm

class DeclValToDeclVarDesugarerTest extends UnitTestSuite {

  test("desugar") {
    val declVal = q"private val x: Int"
    val expectedDeclVar = q"private final var x: Int"

    desugar(declVal).structure shouldBe expectedDeclVar.structure
  }

}
