package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.{Name, XtensionQuasiquoteTerm, XtensionQuasiquoteType}

class NameRendererImplTest extends UnitTestSuite {
  private val nameIndeterminateRenderer = mock[NameIndeterminateRenderer]
  private val termNameRenderer = mock[TermNameRenderer]
  private val typeNameRenderer = mock[TypeNameRenderer]


  private val nameRenderer = new NameRendererImpl(
    nameIndeterminateRenderer,
    termNameRenderer,
    typeNameRenderer)

  test("render for Name.Anonymous") {
    nameRenderer.render(Name.Anonymous())
  }

  test("render for Name.Indeterminate") {
    val name = Name.Indeterminate("myName")

    nameRenderer.render(name)

    verify(nameIndeterminateRenderer).render(eqTree(name))
  }

  test("render for Term.Name") {
    val name = q"myTermName"

    nameRenderer.render(name)

    verify(termNameRenderer).render(eqTree(name))
  }

  test("render for Type.Name") {
    val name = t"myTypeName"

    nameRenderer.render(name)

    verify(typeNameRenderer).render(eqTree(name))
  }
}
