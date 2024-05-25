package io.github.effiban.scala2java.core.desugarers.syntactic

import io.github.effiban.scala2java.core.classifiers.TermApplyInfixClassifier
import io.github.effiban.scala2java.core.entities.TermApplyInfixKind.{Association, Operator, Range}
import io.github.effiban.scala2java.core.entities.TermNames
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.{Lit, Term, XtensionQuasiquoteTerm}

class CompositeTermApplyInfixDesugarerTest extends UnitTestSuite {
  private val classifier = mock[TermApplyInfixClassifier]
  private val associationDesugarer = mock[TermApplyInfixDesugarer]
  private val rangeDesugarer = mock[TermApplyInfixDesugarer]

  private val desugarerMap = Map(
    Association -> associationDesugarer,
    Range -> rangeDesugarer,
  )

  private val compositeTermApplyInfixDesugarer = new CompositeTermApplyInfixDesugarer(classifier, desugarerMap)

  test("desugar() when desugarer exists for classified type") {
    val termApplyInfix = Term.ApplyInfix(
      lhs = Lit.Int(1),
      op = TermNames.ScalaUntil,
      targs = Nil,
      args = List(Lit.Int(10))
    )

    val termApply = Term.Apply(
      fun = TermNames.ScalaRange,
      args = List(Lit.Int(1), Lit.Int(10))
    )

    when(classifier.classify(eqTree(termApplyInfix))).thenReturn(Range)
    doReturn(termApply).when(rangeDesugarer).desugar(eqTree(termApplyInfix))
    compositeTermApplyInfixDesugarer.desugar(termApplyInfix).structure shouldBe termApply.structure
  }

  test("desugar() when no desugarer exists for classified type") {
    val termApply = q"a + b"
    when(classifier.classify(eqTree(termApply))).thenReturn(Operator)
    compositeTermApplyInfixDesugarer.desugar(termApply).structure shouldBe termApply.structure
  }

}
