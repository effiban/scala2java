package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.{Lit, Pat, Term}

class PatRendererImplTest extends UnitTestSuite {
  private val TermName: Term.Name = Term.Name("x")

  private val litRenderer = mock[LitRenderer]
  private val termNameRenderer = mock[TermNameRenderer]
  private val patWildcardRenderer = mock[PatWildcardRenderer]
  private val patSeqWildcardRenderer = mock[PatSeqWildcardRenderer]
  private val patVarRenderer = mock[PatVarRenderer]
  private val patTupleRenderer = mock[PatTupleRenderer]
  private val patExtractRenderer = mock[PatExtractRenderer]
  private val patInterpolateRenderer = mock[PatInterpolateRenderer]

  val patRenderer = new PatRendererImpl(
    litRenderer,
    termNameRenderer,
    patWildcardRenderer,
    patSeqWildcardRenderer,
    patVarRenderer,
    patTupleRenderer,
    patExtractRenderer,
    patInterpolateRenderer
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

  test("render Pat.SeqWildcard") {
    patRenderer.render(Pat.SeqWildcard())
    verify(patSeqWildcardRenderer).render(eqTree(Pat.SeqWildcard()))
  }

  test("render Pat.Var") {
    patRenderer.render(Pat.Var(TermName))
    verify(patVarRenderer).render(eqTree(Pat.Var(TermName)))
  }

  test("render Alternative") {
    // TODO
  }

  test("render Pat.Tuple") {
    val tuple = Pat.Tuple(List(Lit.String("myName"), Lit.Int(2), Lit.Boolean(true)))
    patRenderer.render(tuple)
    verify(patTupleRenderer).render(eqTree(tuple))
  }

  test("render Pat.Extract") {
    val patExtract = Pat.Extract(fun = Term.Name("MyRecord"), args = List(Pat.Var(TermName), Lit.Int(3)))
    patRenderer.render(patExtract)
    verify(patExtractRenderer).render(eqTree(patExtract))
  }

  test("render Pat.Interpolate") {
    val patInterpolate = Pat.Interpolate(
      prefix = Term.Name("r"),
      parts = List(Lit.String("Hello "), Lit.String(", have a (.+) day")),
      args = List(Term.Name("name"))
    )
    patRenderer.render(patInterpolate)
    verify(patInterpolateRenderer).render(eqTree(patInterpolate))
  }

  test("render Pat.Typed") {
    // TODO
  }

}
