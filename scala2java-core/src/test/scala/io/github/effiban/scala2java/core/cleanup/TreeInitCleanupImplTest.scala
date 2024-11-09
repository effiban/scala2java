package io.github.effiban.scala2java.core.cleanup

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.{XtensionQuasiquoteTemplate, XtensionQuasiquoteTerm}

class TreeInitCleanupImplTest extends UnitTestSuite {

  private val templateInitCleanup = mock[TemplateInitCleanup]
  
  private val treeInitCleanup = new TreeInitCleanupImpl(templateInitCleanup)

  test("cleanup of template should call inner cleanup") {

    val initialTemplate =
      template"""
      A with B {
        val b: B = 3
      }
      """

    val finalTemplate =
      template"""
      A {
        val b: B = 3
      }
      """

    when(templateInitCleanup.cleanup(eqTree(initialTemplate))).thenReturn(finalTemplate)

    treeInitCleanup.cleanup(initialTemplate).structure shouldBe finalTemplate.structure
  }

  test("cleanup of class should call inner cleanup for template") {

    val initialClass =
      q"""
      class A extends B with C {
        val d: D = 3
      }
      """

    val finalClass =
      q"""
      class A extends B {
        val d: D = 3
      }
      """

    val initialTemplate =
      template"""
      B with C {
        val d: D = 3
      }
      """

    val finalTemplate =
      template"""
      B {
        val d: D = 3
      }
      """

    when(templateInitCleanup.cleanup(eqTree(initialTemplate))).thenReturn(finalTemplate)

    treeInitCleanup.cleanup(initialClass).structure shouldBe finalClass.structure
  }

  test("cleanup of Defn.Val should return unchanged") {

    val defnVal = q"val x = 3"

    treeInitCleanup.cleanup(defnVal).structure shouldBe defnVal.structure
  }

}
