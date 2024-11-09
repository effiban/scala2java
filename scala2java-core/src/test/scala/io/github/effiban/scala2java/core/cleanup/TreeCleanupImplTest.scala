package io.github.effiban.scala2java.core.cleanup

import io.github.effiban.scala2java.core.importmanipulation.PkgImportRemover
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.{XtensionQuasiquoteTemplate, XtensionQuasiquoteTerm}

class TreeCleanupImplTest extends UnitTestSuite {

  private val pkgImportRemover = mock[PkgImportRemover]

  private val treeCleanup = new TreeCleanupImpl(pkgImportRemover)

  test("cleanup of package when pkg import remover removes some imports should return without them") {
    val initialPkg =
      q"""
      package a {
        import java.lang.String
        import java.lang.IllegalArgumentException
        import b.B
        import c.C
      }
      """

    val finalPkg =
      q"""
      package a {
        import b.B
        import c.C
      }
      """

    when(pkgImportRemover.removeJavaLangFrom(eqTree(initialPkg))).thenReturn(finalPkg)

    treeCleanup.cleanup(initialPkg).structure shouldBe finalPkg.structure
  }

  test("cleanup of Defn.Val should return unchanged") {

    val defnVal = q"val x = 3"

    treeCleanup.cleanup(defnVal).structure shouldBe defnVal.structure
  }
}
