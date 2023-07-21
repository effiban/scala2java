package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.entities.JavaModifier
import io.github.effiban.scala2java.core.renderers.contexts.DeclRenderContext
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchersSugar.eqTo

import scala.meta.XtensionQuasiquoteTerm

class DefaultStatRendererImplTest extends UnitTestSuite {

  private val statTermRenderer: StatTermRenderer = mock[StatTermRenderer]
  private val importRenderer: ImportRenderer = mock[ImportRenderer]
  private val declRenderer: DeclRenderer = mock[DeclRenderer]

  private val defaultStatRenderer: DefaultStatRenderer = new DefaultStatRendererImpl(
    statTermRenderer,
    importRenderer,
    declRenderer
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

  test("render() for Decl.Var when has correct context") {
    val declVar = q"private var x: Int"
    val context = DeclRenderContext(javaModifiers = List(JavaModifier.Private))
    defaultStatRenderer.render(declVar, context)
    verify(declRenderer).render(eqTree(declVar), eqTo(context))
  }

  test("render() for Decl.Var when has incorrect context") {
    val declVar = q"private var x: Int"
    intercept[IllegalStateException] {
      defaultStatRenderer.render(declVar)
    }
  }
}
