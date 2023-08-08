package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.StatContext
import io.github.effiban.scala2java.core.entities.JavaModifier
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.traversers.results.DeclDefTraversalResult
import io.github.effiban.scala2java.spi.entities.JavaScope
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchersSugar.eqTo

import scala.meta.XtensionQuasiquoteTerm

class DeclTraverserImplTest extends UnitTestSuite {

  private val TheStatContext = StatContext(JavaScope.Class)

  private val declVarTraverser = mock[DeclVarTraverser]
  private val declDefTraverser = mock[DeclDefTraverser]

  private val declTraverser = new DeclTraverserImpl(declVarTraverser, declDefTraverser)

  test("traverse() a Decl.Var") {
    val declVar = q"private var myVar: Int"
    val traversedDeclVar = q"var myTraversedVar: Int"

    doReturn(traversedDeclVar).when(declVarTraverser).traverse(eqTree(declVar), eqTo(TheStatContext))
    
    declTraverser.traverse(declVar, TheStatContext).structure shouldBe traversedDeclVar.structure
  }

  test("traverse() a Decl.Def") {
    val declDef = q"private def myMethod(param1: Int, param2: Int): String"
    val traversedDeclDef = q"private def myMethod(param11: Int, param22: Int): String"
    val javaModifiers = List(JavaModifier.Private)
    val traversalResult = DeclDefTraversalResult(traversedDeclDef, javaModifiers)

    doReturn(traversalResult).when(declDefTraverser).traverse(eqTree(declDef), eqTo(TheStatContext))

    declTraverser.traverse(declDef, TheStatContext).structure shouldBe traversedDeclDef.structure
  }
}
