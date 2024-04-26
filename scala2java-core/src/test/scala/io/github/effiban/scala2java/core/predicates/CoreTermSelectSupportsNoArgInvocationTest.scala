package io.github.effiban.scala2java.core.predicates

import io.github.effiban.scala2java.core.classifiers.{TermSelectClassifier, TypeClassifier}
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.spi.contexts.TermSelectInferenceContext
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.{Type, XtensionQuasiquoteTerm, XtensionQuasiquoteType}

class CoreTermSelectSupportsNoArgInvocationTest extends UnitTestSuite {

  private val EmptyContext = TermSelectInferenceContext()

  private val termSelectClassifier = mock[TermSelectClassifier]
  private val typeClassifier = mock[TypeClassifier[Type]]

  private val coreTermSelectSupportsNoArgInvocation = new CoreTermSelectSupportsNoArgInvocation(
    termSelectClassifier,
    typeClassifier)

  test("apply() for 'x.y.apply' when 'x.y' has apply() method should return true") {
    val termSelect = q"x.y.apply"
    val qual = q"x.y"

    when(termSelectClassifier.hasApplyMethod(eqTree(qual))).thenReturn(true)

    coreTermSelectSupportsNoArgInvocation(termSelect, EmptyContext) shouldBe true
  }

  test("apply() for 'x.y.apply' when 'x.y' does not have apply() method should return false") {
    val termSelect = q"x.y.apply"
    val qual = q"x.y"

    when(termSelectClassifier.hasApplyMethod(eqTree(qual))).thenReturn(false)

    coreTermSelectSupportsNoArgInvocation(termSelect, EmptyContext) shouldBe false
  }

  test("apply() for 'x.y.empty' when 'x.y' has empty() method should return true") {
    val termSelect = q"x.y.empty"
    val qual = q"x.y"

    when(termSelectClassifier.hasEmptyMethod(eqTree(qual))).thenReturn(true)

    coreTermSelectSupportsNoArgInvocation(termSelect, EmptyContext) shouldBe true
  }

  test("apply() 'for x.y.empty' when 'x.y' does not have empty() method should return false") {
    val termSelect = q"x.y.empty"
    val qual = q"x.y"

    when(termSelectClassifier.hasEmptyMethod(eqTree(qual))).thenReturn(false)

    coreTermSelectSupportsNoArgInvocation(termSelect, EmptyContext) shouldBe false
  }

  test("apply() when name is 'toString' should return true") {
    val termSelect = q"x.toString"

    coreTermSelectSupportsNoArgInvocation(termSelect, EmptyContext) shouldBe true
  }

  test("apply() when qualifier type is list-like and name is 'length' should return true") {
    val termSelect = q"List(1, 2).length"
    val qualType = t"List[Int]"
    val context = TermSelectInferenceContext(Some(qualType))

    when(typeClassifier.isJavaListLike(eqTree(qualType))).thenReturn(true)

    coreTermSelectSupportsNoArgInvocation(termSelect, context) shouldBe true
  }

  test("apply() when qualifier type is list-like and name is 'map' should return false") {
    val termSelect = q"List(1, 2).map"
    val qualType = t"List[Int]"
    val context = TermSelectInferenceContext(Some(qualType))

    when(typeClassifier.isJavaListLike(eqTree(qualType))).thenReturn(true)

    coreTermSelectSupportsNoArgInvocation(termSelect, context) shouldBe false
  }

  test("apply() when qualifier type is not list-like and name is 'length' should return false") {
    val termSelect = q"a.length"
    val qualType = t"A"
    val context = TermSelectInferenceContext(Some(qualType))

    when(typeClassifier.isJavaListLike(eqTree(qualType))).thenReturn(false)

    coreTermSelectSupportsNoArgInvocation(termSelect, context) shouldBe false
  }

  test("apply() when name is 'blabla' should return false") {
    val termSelect = q"x.blabla"

    coreTermSelectSupportsNoArgInvocation(termSelect, EmptyContext) shouldBe false
  }
}
