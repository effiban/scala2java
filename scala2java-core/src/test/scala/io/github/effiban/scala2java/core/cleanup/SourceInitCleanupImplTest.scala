package io.github.effiban.scala2java.core.cleanup

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.XtensionQuasiquoteSource

class SourceInitCleanupImplTest extends UnitTestSuite {

  private val treeInitCleanup = mock[TreeInitCleanup]

  private val sourceInitCleanup = new SourceInitCleanupImpl(treeInitCleanup)

  test("cleanup") {
    val initialSource =
      source"""
      package a.b

      class C extends D {
        val e: E = 3
      }
      """

    val finalSource =
      source"""
      package a.b

      class C {
        val e: E = 3
      }
      """

    when(treeInitCleanup.cleanup(eqTree(initialSource))).thenReturn(finalSource)

    sourceInitCleanup.cleanup(initialSource).structure shouldBe finalSource.structure
  }

}
