package io.github.effiban.scala2java.core.cleanup

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.{Tree, XtensionQuasiquoteSource}

class SourceCleanupImplTest extends UnitTestSuite {

  private val treeCleanup = mock[TreeCleanup]

  private val sourceCleanup = new SourceCleanupImpl(treeCleanup)

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

    when(treeCleanup.cleanup(eqTree(initialSource))).thenReturn(finalSource)

    sourceCleanup.cleanup(initialSource).structure shouldBe finalSource.structure
  }

}
