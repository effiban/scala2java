package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.Term.{Super, This}
import scala.meta.{Name, Term}

class DefaultTermRefRendererImplTest extends UnitTestSuite {

  private val thisRenderer = mock[ThisRenderer]
  private val superRenderer = mock[SuperRenderer]
  private val termNameRenderer = mock[TermNameRenderer]
  private val defaultTermSelectRenderer = mock[DefaultTermSelectRenderer]

  private val defaultTermRefRenderer = new DefaultTermRefRendererImpl(
    thisRenderer,
    superRenderer,
    termNameRenderer,
    defaultTermSelectRenderer
  )

  test("render 'this'") {
    val `this` = This(Name.Indeterminate("MyName"))

    defaultTermRefRenderer.render(`this`)

    verify(thisRenderer).render(eqTree(`this`))
  }

  test("render 'super'") {
    val `super` = Super(thisp = Name.Indeterminate("superName"), superp = Name.Anonymous())

    defaultTermRefRenderer.render(`super`)

    verify(superRenderer).render(eqTree(`super`))
  }

  test("render termName") {
    val termName = Term.Name("x")

    defaultTermRefRenderer.render(termName)

    verify(termNameRenderer).render(eqTree(termName))
  }

  test("render termSelect") {
    val termSelect = Term.Select(Term.Name("X"), Term.Name("x"))

    defaultTermRefRenderer.render(termSelect)

    verify(defaultTermSelectRenderer).render(eqTree(termSelect))
  }
}
