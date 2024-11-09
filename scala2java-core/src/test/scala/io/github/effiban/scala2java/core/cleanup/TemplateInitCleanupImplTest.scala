package io.github.effiban.scala2java.core.cleanup

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.spi.predicates.TemplateInitExcludedPredicate
import org.mockito.ArgumentMatchersSugar.any

import scala.meta.{Init, XtensionQuasiquoteInit, XtensionQuasiquoteTemplate}

class TemplateInitCleanupImplTest extends UnitTestSuite {

  private val templateInitExcludedPredicate = mock[TemplateInitExcludedPredicate]

  private val templateInitCleanup = new TemplateInitCleanupImpl(templateInitExcludedPredicate)

  test("cleanup when none of the inits is excluded") {
    val template =
      template"""
      A with B {
        val c: C
      }
      """

    when(templateInitExcludedPredicate(any[Init])).thenReturn(false)

    templateInitCleanup.cleanup(template).structure shouldBe template.structure
  }

  test("cleanup when some of the inits are excluded") {
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

    doAnswer((init: Init) => init match {
      case init"B" | init"D" => true
      case _ => false
    }).when(templateInitExcludedPredicate)(any[Init])

    templateInitCleanup.cleanup(initialTemplate).structure shouldBe finalTemplate.structure
  }
}
