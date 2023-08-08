package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.XtensionQuasiquoteTerm

class DeclTraverserImplTest extends UnitTestSuite {

  private val declVarTraverser = mock[DeclVarTraverser]
  private val declDefTraverser = mock[DeclDefTraverser]

  private val declTraverser = new DeclTraverserImpl(declVarTraverser, declDefTraverser)

  test("traverse() a Decl.Var") {
    val declVar = q"private var myVar: Int"
    val traversedDeclVar = q"var myTraversedVar: Int"

    doReturn(traversedDeclVar).when(declVarTraverser).traverse(eqTree(declVar))
    
    declTraverser.traverse(declVar).structure shouldBe traversedDeclVar.structure
  }

  test("traverse() a Decl.Def") {
    val declDef = q"private def myMethod(param1: Int, param2: Int): String"
    val traversedDeclDef = q"private def myMethod(param11: Int, param22: Int): String"

    doReturn(traversedDeclDef).when(declDefTraverser).traverse(eqTree(declDef))

    declTraverser.traverse(declDef).structure shouldBe traversedDeclDef.structure
  }
}
