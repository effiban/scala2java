package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.StatContext
import io.github.effiban.scala2java.core.entities.JavaModifier
import io.github.effiban.scala2java.core.renderers.DeclVarRenderer
import io.github.effiban.scala2java.core.renderers.contexts.VarRenderContext
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.testtrees.TypeNames
import io.github.effiban.scala2java.core.traversers.results.DeclVarTraversalResult
import io.github.effiban.scala2java.spi.entities.JavaScope
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchersSugar.eqTo

import scala.meta.{Decl, Term, XtensionQuasiquoteTerm}

class DeclTraverserImplTest extends UnitTestSuite {

  private val TheStatContext = StatContext(JavaScope.Class)

  private val declVarTraverser = mock[DeclVarTraverser]
  private val declVarRenderer = mock[DeclVarRenderer]
  private val declDefTraverser = mock[DeclDefTraverser]

  private val declTraverser = new DeclTraverserImpl(
    declVarTraverser,
    declVarRenderer,
    declDefTraverser)

  test("traverse() a Decl.Var") {
    val declVar = q"private var myVar: Int"
    val traversedDeclVar = q"var myTraversedVar: Int"
    val javaModifiers = List(JavaModifier.Private)
    val traversalResult = DeclVarTraversalResult(traversedDeclVar, javaModifiers)

    doReturn(traversalResult).when(declVarTraverser).traverse(eqTree(declVar), eqTo(TheStatContext))
    
    declTraverser.traverse(declVar, TheStatContext)

    verify(declVarRenderer).render(eqTree(traversedDeclVar), eqTo(VarRenderContext(javaModifiers)))
  }

  test("traverse() a Decl.Def") {

    val declDef = Decl.Def(
      mods = List(),
      name = Term.Name("myMethod"),
      tparams = List(),
      paramss = List(),
      decltpe = TypeNames.Int
    )

    declTraverser.traverse(declDef, TheStatContext)

    verify(declDefTraverser).traverse(eqTree(declDef), eqTo(TheStatContext))
  }
}
