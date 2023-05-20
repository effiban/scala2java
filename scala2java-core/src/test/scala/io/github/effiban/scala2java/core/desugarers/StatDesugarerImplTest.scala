package io.github.effiban.scala2java.core.desugarers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.XtensionQuasiquoteTerm

class StatDesugarerImplTest extends UnitTestSuite {

  private val defnDesugarer = mock[DefnDesugarer]
  private val declDesugarer = mock[DeclDesugarer]
  private val evaluatedTermDesugarer = mock[EvaluatedTermDesugarer]

  private val statDesugarer = new StatDesugarerImpl(
    defnDesugarer,
    declDesugarer,
    evaluatedTermDesugarer
  )

  test("desugar Defn") {
    val defn = q"val x = calc"
    val desugaredDefn = q"val x = calc()"

    doReturn(desugaredDefn).when(defnDesugarer).desugar(eqTree(defn))

    statDesugarer.desugar(defn).structure shouldBe desugaredDefn.structure
  }

  test("desugar Decl") {
    val decl = q"def foo(x: Int = calc): Unit"
    val desugaredDecl = q"def foo(x: Int = calc()): Unit"

    doReturn(desugaredDecl).when(declDesugarer).desugar(eqTree(decl))

    statDesugarer.desugar(decl).structure shouldBe desugaredDecl.structure
  }

  test("desugar Term") {
    val term = q"func"
    val desugaredTerm = q"func()"

    doReturn(desugaredTerm).when(evaluatedTermDesugarer).desugar(term)

    statDesugarer.desugar(term).structure shouldBe desugaredTerm.structure
  }

  test("desugar Import") {
    val `import` = q"import a.b.c"

    statDesugarer.desugar(`import`).structure shouldBe `import`.structure
  }
}
