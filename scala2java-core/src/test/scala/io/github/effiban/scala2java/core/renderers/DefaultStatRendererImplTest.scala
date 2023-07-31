package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.entities.JavaModifier
import io.github.effiban.scala2java.core.renderers.contexts._
import io.github.effiban.scala2java.core.renderers.matchers.PkgRenderContextMockitoMatcher.eqPkgRenderContext
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchersSugar.eqTo

import scala.meta.XtensionQuasiquoteTerm

class DefaultStatRendererImplTest extends UnitTestSuite {

  private val statTermRenderer: StatTermRenderer = mock[StatTermRenderer]
  private val importRenderer: ImportRenderer = mock[ImportRenderer]
  private val pkgRenderer: PkgRenderer = mock[PkgRenderer]
  private val declRenderer: DeclRenderer = mock[DeclRenderer]
  private val defnRenderer: DefnRenderer = mock[DefnRenderer]

  private val defaultStatRenderer: DefaultStatRenderer = new DefaultStatRendererImpl(
    statTermRenderer,
    importRenderer,
    pkgRenderer,
    declRenderer,
    defnRenderer
  )

  test("render() for Term.Apply") {
    val termApply = q"foo(1)"
    defaultStatRenderer.render(termApply)
    verify(statTermRenderer).render(eqTree(termApply))
  }

  test("render() for Import") {
    val `import` = q"import a.b.c"
    defaultStatRenderer.render(`import`)
    verify(importRenderer).render(eqTree(`import`))
  }

  test("render() for Pkg when has correct context") {
    val cls =
      q"""
      class D {
      }
      """

    val pkg =
      q"""
      package a.b.c {
        class D {
        }
      }
      """

    val context = PkgRenderContext(
      Map(cls -> RegularClassRenderContext())
    )

    defaultStatRenderer.render(pkg, context)

    verify(pkgRenderer).render(eqTree(pkg), eqPkgRenderContext(context))
  }

  test("render() for Pkg when has incorrect context should throw exception") {
    val pkg =
      q"""
      package a.b.c {
        class D {
        }
      }
      """

    intercept[IllegalStateException] {
      defaultStatRenderer.render(pkg, RegularClassRenderContext())
    }
  }

  test("render() for Decl.Var when has correct non-empty context") {
    val declVar = q"private var x: Int"
    val context = VarRenderContext(javaModifiers = List(JavaModifier.Private))
    defaultStatRenderer.render(declVar, context)
    verify(declRenderer).render(eqTree(declVar), eqTo(context))
  }

  test("render() for Decl.Var when has empty context") {
    val declVar = q"private var x: Int"
    defaultStatRenderer.render(declVar)
    verify(declRenderer).render(eqTree(declVar), eqTo(EmptyStatRenderContext))
  }

  test("render() for Decl.Var when has incorrect context should throw exception") {
    val declVar = q"private var x: Int"
    intercept[IllegalStateException] {
      defaultStatRenderer.render(declVar, RegularClassRenderContext())
    }
  }

  test("render() for Defn.Var when has correct non-empty context") {
    val defnVar = q"private var x: Int = 3"
    val context = DefRenderContext(javaModifiers = List(JavaModifier.Private))
    defaultStatRenderer.render(defnVar, context)
    verify(defnRenderer).render(eqTree(defnVar), eqTo(context))
  }

  test("render() for Defn.Var when has empty context") {
    val defnVar = q"private var x: Int = 3"
    defaultStatRenderer.render(defnVar)
    verify(defnRenderer).render(eqTree(defnVar), eqTo(EmptyStatRenderContext))
  }

  test("render() for Defn.Var when has incorrect context should throw exception") {
    val defnVar = q"private var x: Int = 3"
    intercept[IllegalStateException] {
      defaultStatRenderer.render(defnVar, PkgRenderContext())
    }
  }
}
