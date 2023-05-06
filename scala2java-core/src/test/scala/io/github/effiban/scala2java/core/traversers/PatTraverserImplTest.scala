package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.Pat.{Alternative, Bind}
import scala.meta.{Pat, XtensionQuasiquoteCaseOrPattern, XtensionQuasiquoteTerm}

class PatTraverserImplTest extends UnitTestSuite {

  private val patSeqWildcardTraverser = mock[PatSeqWildcardTraverser]
  private val bindTraverser = mock[BindTraverser]
  private val alternativeTraverser = mock[AlternativeTraverser]
  private val patTupleTraverser = mock[PatTupleTraverser]
  private val patExtractTraverser = mock[PatExtractTraverser]
  private val patExtractInfixTraverser = mock[PatExtractInfixTraverser]
  private val patInterpolateTraverser = mock[PatInterpolateTraverser]
  private val patTypedTraverser = mock[PatTypedTraverser]

  val patTraverser = new PatTraverserImpl(
    patSeqWildcardTraverser,
    bindTraverser,
    alternativeTraverser,
    patTupleTraverser,
    patExtractTraverser,
    patExtractInfixTraverser,
    patInterpolateTraverser,
    patTypedTraverser)


  test("traverse Lit.Int") {
    val lit = q"3"
    patTraverser.traverse(lit).structure shouldBe lit.structure
  }

  test("traverse Term.Name") {
    val termName = q"x"
    patTraverser.traverse(termName).structure shouldBe termName.structure
  }

  test("traverse Pat.Wildcard") {
    patTraverser.traverse(Pat.Wildcard()).structure shouldBe Pat.Wildcard().structure
  }

  test("traverse Pat.SeqWildcard") {
    doReturn(Pat.SeqWildcard()).when(patSeqWildcardTraverser).traverse(eqTree(Pat.SeqWildcard()))
    patTraverser.traverse(Pat.SeqWildcard()).structure shouldBe Pat.SeqWildcard().structure
  }

  test("traverse Pat.Var") {
    val patVar = p"x"
    patTraverser.traverse(patVar).structure shouldBe patVar.structure
  }

  test("traverse Bind") {
    val bind = Bind(lhs = p"x", rhs = p"y")
    doReturn(bind).when(bindTraverser).traverse(eqTree(bind))
    patTraverser.traverse(bind).structure shouldBe bind.structure
  }

  test("traverse Alternative") {
    val alternative = Alternative(lhs = p"1", rhs = p"2")
    val traversedAlternative = Alternative(lhs = p"11", rhs = p"22")
    doReturn(traversedAlternative).when(alternativeTraverser).traverse(eqTree(alternative))
    patTraverser.traverse(alternative).structure shouldBe traversedAlternative.structure
  }

  test("traverse Pat.Tuple") {
    val tuple = p"""("myName", 2, true)"""
    doReturn(tuple).when(patTupleTraverser).traverse(eqTree(tuple))
    patTraverser.traverse(tuple).structure shouldBe tuple.structure
  }

  test("traverse Pat.Extract") {
    val patExtract = p"MyRecord(x, 3)"
    doReturn(patExtract).when(patExtractTraverser).traverse(eqTree(patExtract))
    patTraverser.traverse(patExtract).structure shouldBe patExtract.structure
  }

  test("traverse Pat.ExtractInfix") {
    val patExtractInfix = p"x MyRecord 3"
    val expectedPatExtract = p"MyRecord(x, 3)"
    doReturn(expectedPatExtract).when(patExtractInfixTraverser).traverse(eqTree(patExtractInfix))
    patTraverser.traverse(patExtractInfix).structure shouldBe expectedPatExtract.structure
  }

  test("traverse Pat.Interpolate") {
    val patInterpolate = Pat.Interpolate(prefix = q"r",
      parts = List(q""""Hello """", q"""", have a (.+) day""""),
      args = List(q"name")
    )
    doReturn(patInterpolate).when(patInterpolateTraverser).traverse(eqTree(patInterpolate))
    patTraverser.traverse(patInterpolate).structure shouldBe patInterpolate.structure
  }

  test("traverse Pat.Typed") {
    val patTyped = p"x: MyType"
    val traversedPatTyped = p"y: MyTraversedType"
    doReturn(traversedPatTyped).when(patTypedTraverser).traverse(eqTree(patTyped))
    patTraverser.traverse(patTyped).structure shouldBe traversedPatTyped.structure
  }
}
