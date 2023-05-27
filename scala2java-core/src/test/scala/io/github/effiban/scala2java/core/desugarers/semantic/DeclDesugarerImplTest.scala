package io.github.effiban.scala2java.core.desugarers.semantic

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.XtensionQuasiquoteTerm

class DeclDesugarerImplTest extends UnitTestSuite {

  private val declDefDesugarer = mock[DeclDefDesugarer]

  private val declDesugarer = new DeclDesugarerImpl(declDefDesugarer)

  test("desugar Decl.Def") {
    val declDef = q"def foo(x: Int = func)"

    val desugaredDeclDef = q"def foo(x: Int = func())"

    doReturn(desugaredDeclDef).when(declDefDesugarer).desugar(eqTree(declDef))

    declDesugarer.desugar(declDef).structure shouldBe desugaredDeclDef.structure

  }

  test("desugar Decl.Val") {
    val declVal = q"val x: Int"
    declDesugarer.desugar(declVal).structure shouldBe declVal.structure
  }

}
