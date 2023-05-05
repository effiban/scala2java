package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.renderers._
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.Pat.{Alternative, Bind}
import scala.meta.{Lit, Pat, Term, Type}

class PatTraverserImplTest extends UnitTestSuite {

  private val TermName: Term.Name = Term.Name("x")

  private val litRenderer = mock[LitRenderer]
  private val termNameRenderer = mock[TermNameRenderer]
  private val patWildcardRenderer = mock[PatWildcardRenderer]
  private val patSeqWildcardTraverser = mock[PatSeqWildcardTraverser]
  private val patVarRenderer = mock[PatVarRenderer]
  private val bindTraverser = mock[BindTraverser]
  private val alternativeTraverser = mock[AlternativeTraverser]
  private val patTupleTraverser = mock[PatTupleTraverser]
  private val patExtractTraverser = mock[PatExtractTraverser]
  private val patExtractRenderer = mock[PatExtractRenderer]
  private val patExtractInfixTraverser = mock[PatExtractInfixTraverser]
  private val patInterpolateTraverser = mock[PatInterpolateTraverser]
  private val patInterpolateRenderer = mock[PatInterpolateRenderer]
  private val patTypedTraverser = mock[PatTypedTraverser]

  val patTraverser = new PatTraverserImpl(
    litRenderer,
    termNameRenderer,
    patWildcardRenderer,
    patSeqWildcardTraverser,
    patVarRenderer,
    bindTraverser,
    alternativeTraverser,
    patTupleTraverser,
    patExtractTraverser,
    patExtractRenderer,
    patExtractInfixTraverser,
    patInterpolateTraverser,
    patInterpolateRenderer,
    patTypedTraverser)


  test("traverse Lit.Int") {
    val lit = Lit.Int(3)
    patTraverser.traverse(lit)
    verify(litRenderer).render(eqTree(lit))
  }

  test("traverse Term.Name") {
    patTraverser.traverse(TermName)
    verify(termNameRenderer).render(eqTree(TermName))
  }

  test("traverse Pat.Wildcard") {
    patTraverser.traverse(Pat.Wildcard())
    verify(patWildcardRenderer).render(eqTree(Pat.Wildcard()))
  }

  test("traverse Pat.SeqWildcard") {
    doReturn(Pat.SeqWildcard()).when(patSeqWildcardTraverser).traverse(eqTree(Pat.SeqWildcard()))
    patTraverser.traverse(Pat.SeqWildcard())
  }

  test("traverse Pat.Var") {
    patTraverser.traverse(Pat.Var(TermName))
    verify(patVarRenderer).render(eqTree(Pat.Var(TermName)))
  }

  test("traverse Bind") {
    val bind = Bind(lhs = Pat.Var(TermName), rhs = Term.Name("X"))
    doReturn(bind).when(bindTraverser).traverse(eqTree(bind))
    patTraverser.traverse(bind)
  }

  test("traverse Alternative") {
    val alternative = Alternative(lhs = Lit.Int(2), rhs = Lit.Int(3))
    patTraverser.traverse(alternative)
    verify(alternativeTraverser).traverse(eqTree(alternative))
  }

  test("traverse Pat.Tuple") {
    val tuple = Pat.Tuple(List(Lit.String("myName"), Lit.Int(2), Lit.Boolean(true)))
    doReturn(tuple).when(patTupleTraverser).traverse(eqTree(tuple))
    patTraverser.traverse(tuple)
  }

  test("traverse Pat.Extract") {
    val patExtract = Pat.Extract(fun = Term.Name("MyRecord"), args = List(Pat.Var(TermName), Lit.Int(3)))
    doReturn(patExtract).when(patExtractTraverser).traverse(eqTree(patExtract))
    patTraverser.traverse(patExtract)
    verify(patExtractRenderer).render(eqTree(patExtract))
  }

  test("traverse Pat.ExtractInfix") {
    val patExtractInfix = Pat.ExtractInfix(lhs = Pat.Var(TermName), op = Term.Name("MyRecord"), rhs = List(Lit.Int(3)))
    val expectedPatExtract = Pat.Extract(fun = Term.Name("MyRecord"), args = List(Pat.Var(TermName), Lit.Int(3)))
    doReturn(expectedPatExtract).when(patExtractInfixTraverser).traverse(eqTree(patExtractInfix))
    patTraverser.traverse(patExtractInfix)
    verify(patExtractRenderer).render(eqTree(expectedPatExtract))
  }

  test("traverse Pat.Interpolate") {
    val patInterpolate = Pat.Interpolate(
      prefix = Term.Name("r"),
      parts = List(Lit.String("Hello "), Lit.String(", have a (.+) day")),
      args = List(Term.Name("name"))
    )
    doReturn(patInterpolate).when(patInterpolateTraverser).traverse(eqTree(patInterpolate))
    patTraverser.traverse(patInterpolate)
    verify(patInterpolateRenderer).render(eqTree(patInterpolate))
  }

  test("traverse Pat.Typed") {
    val patTyped = Pat.Typed(lhs = Pat.Var(TermName), rhs = Type.Name("MyType"))
    patTraverser.traverse(patTyped)
    verify(patTypedTraverser).traverse(eqTree(patTyped))
  }
}
