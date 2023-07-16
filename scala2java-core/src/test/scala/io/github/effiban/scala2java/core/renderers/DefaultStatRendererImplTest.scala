package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.contexts.StatContext
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.spi.entities.JavaScope
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchersSugar.eqTo

import scala.meta.XtensionQuasiquoteTerm

class DefaultStatRendererImplTest extends UnitTestSuite {

  private val statTermRenderer: StatTermRenderer = mock[StatTermRenderer]
  private val importRenderer: ImportRenderer = mock[ImportRenderer]

  private val defaultStatRenderer: DefaultStatRenderer = new DefaultStatRendererImpl(statTermRenderer, importRenderer)

  test("render() for Term.Apply") {
    val termApply = q"foo(1)"
    defaultStatRenderer.render(termApply)
    verify(statTermRenderer).render(eqTree(termApply))
  }

  test("render() for Import") {
    val `import` = q"import a.b.c"
    val context = StatContext(JavaScope.Package)
    defaultStatRenderer.render(`import`, context)
    verify(importRenderer).render(eqTree(`import`), eqTo(context))
  }
}
