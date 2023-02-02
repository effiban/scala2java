package io.github.effiban.scala2java.core.transformers

import io.github.effiban.scala2java.core.classifiers.TermApplyInfixClassifier
import io.github.effiban.scala2java.core.entities.TermApplyInfixKind.{Range, _}
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.testtrees.TermNames
import io.github.effiban.scala2java.spi.transformers.TermApplyInfixToTermApplyTransformer
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.{Lit, Term}

class CoreTermApplyInfixToTermApplyTransformerTest extends UnitTestSuite {

  private val RangeApplyInfix = Term.ApplyInfix(
    lhs = Lit.Int(1),
    op = TermNames.ScalaUntil,
    targs = Nil,
    args = List(Lit.Int(10))
  )

  private val RangeApply = Term.Apply(
    fun = TermNames.ScalaRange,
    args = List(Lit.Int(1), Lit.Int(10))
  )

  private val classifier = mock[TermApplyInfixClassifier]
  private val associationTransformer = mock[TermApplyInfixToTermApplyTransformer]
  private val rangeTransformer = mock[TermApplyInfixToTermApplyTransformer]

  private val transformerMap = Map(
    Association -> associationTransformer,
    Range -> rangeTransformer,
  )

  private val coreTransformer = new CoreTermApplyInfixToTermApplyTransformer(classifier, transformerMap)

  test("transform() when inner transformer returns value") {
    when(classifier.classify(eqTree(RangeApplyInfix))).thenReturn(Range)
    when(rangeTransformer.transform(eqTree(RangeApplyInfix))).thenReturn(Some(RangeApply))
    coreTransformer.transform(RangeApplyInfix).value.structure shouldBe RangeApply.structure
  }

  test("transform() when inner transformer returns empty") {
    when(classifier.classify(eqTree(RangeApplyInfix))).thenReturn(Range)
    when(rangeTransformer.transform(eqTree(RangeApplyInfix))).thenReturn(None)
    coreTransformer.transform(RangeApplyInfix) shouldBe None
  }
}
