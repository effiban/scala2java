package io.github.effiban.scala2java.core.transformers

import io.github.effiban.scala2java.core.classifiers.TermApplyInfixClassifier
import io.github.effiban.scala2java.core.entities.TermApplyInfixKind._
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.spi.transformers.TermApplyInfixToTermApplyTransformer
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.XtensionQuasiquoteTerm

class CoreTermApplyInfixToTermApplyTransformerTest extends UnitTestSuite {

  private val classifier = mock[TermApplyInfixClassifier]
  private val operatorTransformer = mock[TermApplyInfixToTermApplyTransformer]
  private val unclassifiedTransformer = mock[TermApplyInfixToTermApplyTransformer]

  private val transformerMap = Map(
    Operator -> operatorTransformer,
    Unclassified -> unclassifiedTransformer
  )

  private val coreTransformer = new CoreTermApplyInfixToTermApplyTransformer(classifier, transformerMap)

  test("transform() when inner transformer returns value") {
    val termApplyInfix = q"a fun b"
    val termApply = q"fun(a, b)"

    when(classifier.classify(eqTree(termApplyInfix))).thenReturn(Unclassified)
    when(unclassifiedTransformer.transform(eqTree(termApplyInfix))).thenReturn(Some(termApply))
    coreTransformer.transform(termApplyInfix).value.structure shouldBe termApply.structure
  }

  test("transform() when inner transformer returns empty") {
    val termApplyInfix = q"a + b"

    when(classifier.classify(eqTree(termApplyInfix))).thenReturn(Operator)
    when(operatorTransformer.transform(eqTree(termApplyInfix))).thenReturn(None)
    coreTransformer.transform(termApplyInfix) shouldBe None
  }
}
