package io.github.effiban.scala2java.core.desugarers.syntactic

import io.github.effiban.scala2java.core.desugarers.syntactic.DefnTypeToTraitDesugarer.desugar
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite

import scala.meta.XtensionQuasiquoteTerm

class DefnTypeToTraitDesugarerTest extends UnitTestSuite {

  test("desugar()") {
    val defnType = q"private type MyType[T1, T2] = MyOtherType[T1, T2]"
    val expectedTrait = q"private trait MyType[T1, T2] extends MyOtherType[T1, T2]()"

    desugar(defnType).structure shouldBe expectedTrait.structure
  }
}
