package io.github.effiban.scala2java.core.cleanup

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.{XtensionQuasiquoteTemplate, XtensionQuasiquoteType}

class TemplateInitCleanupImplTest extends UnitTestSuite {

  private val templateParentsUsedResolver = mock[TemplateParentsUsedResolver]

  private val templateInitCleanup = new TemplateInitCleanupImpl(templateParentsUsedResolver)

  test("cleanup when all inits are used") {
    val template =
      template"""
      A with B {
        val c: C
      }
      """

    when(templateParentsUsedResolver.resolve(eqTree(template))).thenReturn(List(t"A", t"B"))

    templateInitCleanup.cleanup(template).structure shouldBe template.structure
  }

  test("cleanup when some of the inits are unused") {
    val initialTemplate =
      template"""
      A with B with C with D {
        val e: E
      }
      """

    val finalTemplate =
      template"""
      A with C {
        val e: E
      }
      """

    when(templateParentsUsedResolver.resolve(eqTree(initialTemplate))).thenReturn(List(t"A", t"C"))

    templateInitCleanup.cleanup(initialTemplate).structure shouldBe finalTemplate.structure
  }
}
