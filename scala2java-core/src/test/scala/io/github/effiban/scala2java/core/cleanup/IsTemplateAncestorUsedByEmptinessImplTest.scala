package io.github.effiban.scala2java.core.cleanup

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.cleanup.IsTemplateAncestorUsedByEmptiness.apply
import io.github.effiban.scala2java.core.reflection.ScalaReflectionClassifier
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.{XtensionQuasiquoteTemplate, XtensionQuasiquoteTerm, XtensionQuasiquoteType}

class IsTemplateAncestorUsedByEmptinessImplTest extends UnitTestSuite {

  private val scalaReflectionClassifier = mock[ScalaReflectionClassifier]

  private val isTemplateAncestorUsedByEmptiness = new IsTemplateAncestorUsedByEmptinessImpl(scalaReflectionClassifier)

  test("apply when classifier returns true should return true") {
    val templ = template"A"
    val ancestor = t"B"

    when(scalaReflectionClassifier.isNonTrivialEmptyType(eqTree(ancestor))).thenReturn(true)

    isTemplateAncestorUsedByEmptiness(templ, ancestor) shouldBe true
  }
  test("apply when classifier returns false should return false") {
    val templ = template"A"
    val ancestor = t"B"

    when(scalaReflectionClassifier.isNonTrivialEmptyType(eqTree(ancestor))).thenReturn(false)

    isTemplateAncestorUsedByEmptiness(templ, ancestor) shouldBe false
  }
}

