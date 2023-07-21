package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.entities.JavaModifier
import io.github.effiban.scala2java.core.renderers.contexts.{DefRenderContext, VarRenderContext}
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
    val context = VarRenderContext(javaModifiers)

    declRenderer.render(declVar, context)

    verify(declVarRenderer).render(eqTree(declVar), eqTo(context))
  }

  test("render() a Decl.Var with incorrect context should throw an exception") {
    val declVar = q"private final var x: Int"
    val javaModifiers = List(JavaModifier.Private, JavaModifier.Final)
    val context = DefRenderContext(javaModifiers)

    intercept[IllegalStateException] {
      declRenderer.render(declVar, context)
    }
  }

  test("render() a Decl.Def") {
    val declDef = q"private def foo(x: Int)"
    val javaModifiers = List(JavaModifier.Private)
    val context = DefRenderContext(javaModifiers)

    declRenderer.render(declDef, context)

    verify(declDefRenderer).render(eqTree(declDef), eqTo(DefRenderContext(javaModifiers)))
  }

  test("render() a Decl.Def with incorrect context should throw an exception") {
    val declDef = q"private def foo(x: Int)"
    val javaModifiers = List(JavaModifier.Private)
    val context = VarRenderContext(javaModifiers)

    intercept[IllegalStateException] {
      declRenderer.render(declDef, context)
    }
  }
}
