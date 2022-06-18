package com.effiban.scala2java

import com.effiban.scala2java.matchers.TreeMatcher.eqTree

import scala.meta.Pat.{Alternative, Bind}
import scala.meta.{Lit, Pat, Term, Type}

class PatTraverserImplTest extends UnitTestSuite {

  private val TermName: Term.Name = Term.Name("x")

  private val termNameTraverser = mock[TermNameTraverser]
  private val patWildcardTraverser = mock[PatWildcardTraverser]
  private val patSeqWildcardTraverser = mock[PatSeqWildcardTraverser]
  private val patVarTraverser = mock[PatVarTraverser]
  private val bindTraverser = mock[BindTraverser]
  private val alternativeTraverser = mock[AlternativeTraverser]
  private val patTupleTraverser = mock[PatTupleTraverser]
  private val patExtractTraverser = mock[PatExtractTraverser]
  private val patExtractInfixTraverser = mock[PatExtractInfixTraverser]
  private val patInterpolateTraverser = mock[PatInterpolateTraverser]
  private val patTypedTraverser = mock[PatTypedTraverser]

  val patTraverser = new PatTraverserImpl(
    termNameTraverser,
    patWildcardTraverser,
    patSeqWildcardTraverser,
    patVarTraverser,
    bindTraverser,
    alternativeTraverser,
    patTupleTraverser,
    patExtractTraverser,
    patExtractInfixTraverser,
    patInterpolateTraverser,
    patTypedTraverser)


  test("traverse Term.Name") {
    patTraverser.traverse(TermName)
    verify(termNameTraverser).traverse(eqTree(TermName))
  }

  test("traverse Pat.Wildcard") {
    patTraverser.traverse(Pat.Wildcard())
    verify(patWildcardTraverser).traverse(eqTree(Pat.Wildcard()))
  }

  test("traverse Pat.SeqWildcard") {
    patTraverser.traverse(Pat.SeqWildcard())
    verify(patSeqWildcardTraverser).traverse(eqTree(Pat.SeqWildcard()))
  }

  test("traverse Pat.Var") {
    patTraverser.traverse(Pat.Var(TermName))
    verify(patVarTraverser).traverse(eqTree(Pat.Var(TermName)))
  }

  test("traverse Bind") {
    val bind = Bind(lhs = Pat.Var(TermName), rhs = Term.Name("X"))
    patTraverser.traverse(bind)
    verify(bindTraverser).traverse(eqTree(bind))
  }

  test("traverse Alternative") {
    val alternative = Alternative(lhs = Lit.Int(2), rhs = Lit.Int(3))
    patTraverser.traverse(alternative)
    verify(alternativeTraverser).traverse(eqTree(alternative))
  }

  test("traverse Pat.Tuple") {
    val tuple = Pat.Tuple(List(Lit.String("myName"), Lit.Int(2), Lit.Boolean(true)))
    patTraverser.traverse(tuple)
    verify(patTupleTraverser).traverse(eqTree(tuple))
  }

  test("traverse Pat.Extract") {
    val patExtract = Pat.Extract(fun = Term.Name("MyRecord"), args = List(Pat.Var(TermName), Lit.Int(3)))
    patTraverser.traverse(patExtract)
    verify(patExtractTraverser).traverse(eqTree(patExtract))
  }

  test("traverse Pat.ExtractInfix") {
    val patExtractInfix = Pat.ExtractInfix(lhs = Pat.Var(TermName), op = Term.Name("MyRecord"), rhs = List(Lit.Int(3)))
    patTraverser.traverse(patExtractInfix)
    verify(patExtractInfixTraverser).traverse(eqTree(patExtractInfix))
  }

  test("traverse Pat.Interpolate") {
    val patInterpolate = Pat.Interpolate(
      prefix = Term.Name("r"),
      parts = List(Lit.String("Hello "), Lit.String(", have a (.+) day")),
      args = List(Term.Name("name"))
    )
    patTraverser.traverse(patInterpolate)
    verify(patInterpolateTraverser).traverse(eqTree(patInterpolate))
  }

  test("traverse Pat.Typed") {
    val patTyped = Pat.Typed(lhs = Pat.Var(TermName), rhs = Type.Name("MyType"))
    patTraverser.traverse(patTyped)
    verify(patTypedTraverser).traverse(eqTree(patTyped))
  }
}
