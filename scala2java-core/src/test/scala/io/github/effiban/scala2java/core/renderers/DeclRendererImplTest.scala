package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.entities.JavaModifier
import io.github.effiban.scala2java.core.renderers.contexts.{DeclRenderContext, DefRenderContext, VarRenderContext}
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchersSugar.eqTo

import scala.meta.XtensionQuasiquoteTerm

class DeclRendererImplTest extends UnitTestSuite {

  private val declVarRenderer = mock[DeclVarRenderer]
  private val declDefRenderer = mock[DeclDefRenderer]

  private val declRenderer = new DeclRendererImpl(declVarRenderer, declDefRenderer)

  test("render() a Decl.Var") {
    val declVar = q"private final var x: Int"
    val javaModifiers = List(JavaModifier.Private, JavaModifier.Final)
    val context = DeclRenderContext(javaModifiers)

    declRenderer.render(declVar, context)

    verify(declVarRenderer).render(eqTree(declVar), eqTo(VarRenderContext(javaModifiers)))
  }

  test("render() a Decl.Def") {
    val declDef = q"private def foo(x: Int)"
    val javaModifiers = List(JavaModifier.Private)
    val context = DeclRenderContext(javaModifiers)

    declRenderer.render(declDef, context)

    verify(declDefRenderer).render(eqTree(declDef), eqTo(DefRenderContext(javaModifiers)))
  }
}
