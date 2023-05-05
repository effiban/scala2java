package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.{Lit, Pat, Term, XtensionQuasiquoteCaseOrPattern}

class PatRendererImplTest extends UnitTestSuite {
  private val TermName: Term.Name = Term.Name("x")

  private val litRenderer = mock[LitRenderer]
  private val termNameRenderer = mock[TermNameRenderer]
  private val patWildcardRenderer = mock[PatWildcardRenderer]
  private val patVarRenderer = mock[PatVarRenderer]
  private val alternativeRenderer = mock[AlternativeRenderer]

  val patRenderer = new PatRendererImpl(
    litRenderer,
    termNameRenderer,
    patWildcardRenderer,
    patVarRenderer,
    alternativeRenderer
  )


  test("render Lit.Int") {
    val lit = Lit.Int(3)
    patRenderer.render(lit)
    verify(litRenderer).render(eqTree(lit))
  }

  test("render Term.Name") {
    patRenderer.render(TermName)
    verify(termNameRenderer).render(eqTree(TermName))
  }

  test("render Pat.Wildcard") {
    patRenderer.render(Pat.Wildcard())
    verify(patWildcardRenderer).render(eqTree(Pat.Wildcard()))
  }

  test("render Pat.Var") {
    patRenderer.render(Pat.Var(TermName))
    verify(patVarRenderer).render(eqTree(Pat.Var(TermName)))
  }

  test("render Alternative") {
    val alternative = p"2 | 3"
    patRenderer.render(alternative)
    verify(alternativeRenderer).render(eqTree(alternative))
  }

  test("render Pat.Typed") {
    // TODO
  }

}
