package io.github.effiban.scala2java.core.cleanup

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.{XtensionQuasiquoteTemplate, XtensionQuasiquoteType}

class CompositeIsTemplateAncestorUsedTest extends UnitTestSuite {
  private val predicate1 = mock[IsTemplateAncestorUsed]
  private val predicate2 = mock[IsTemplateAncestorUsed]

  private val template = template"A"
  private val ancestorType = t"B"

  private val compositePredicate = new CompositeIsTemplateAncestorUsed(List(predicate1, predicate2))

  test("apply() when all predicates return true, should return true") {
    when(predicate1.apply(eqTree(template), eqTree(ancestorType))).thenReturn(true)
    when(predicate2.apply(eqTree(template), eqTree(ancestorType))).thenReturn(true)

    compositePredicate.apply(template, ancestorType) shouldBe true
  }

  test("apply() when only one predicate returns true, should return true") {
    when(predicate1.apply(eqTree(template), eqTree(ancestorType))).thenReturn(true)
    when(predicate2.apply(eqTree(template), eqTree(ancestorType))).thenReturn(false)

    compositePredicate.apply(template, ancestorType) shouldBe true
  }

  test("apply() when all predicates return false, should return false") {
    when(predicate1.apply(eqTree(template), eqTree(ancestorType))).thenReturn(false)
    when(predicate2.apply(eqTree(template), eqTree(ancestorType))).thenReturn(false)

    compositePredicate.apply(template, ancestorType) shouldBe false
  }
}
