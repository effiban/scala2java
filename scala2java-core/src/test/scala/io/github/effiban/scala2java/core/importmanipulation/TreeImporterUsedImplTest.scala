package io.github.effiban.scala2java.core.importmanipulation

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.{XtensionQuasiquoteImporter, XtensionQuasiquoteTerm, XtensionQuasiquoteType}

class TreeImporterUsedImplTest extends UnitTestSuite {

  private val typeNameImporterMatcher = mock[TypeNameImporterMatcher]

  private val treeImporterUsed = new TreeImporterUsedImpl(typeNameImporterMatcher)


  test("apply() for a Pkg when has a matching Type.Name should return true") {
    val pkg =
      q"""
      package a.b {
        import d.D

        trait C {
          val x: D
        }
      }
      """

    doReturn(Some(importer"d.D")).when(typeNameImporterMatcher).findMatch(eqTree(t"D"), eqTree(importer"d.D"))

    treeImporterUsed(pkg, importer"d.D") shouldBe true
  }

  test("apply() for a Pkg when has Type.Names and none match should return false") {
  }

  test("apply() for a Pkg when has no Type.Names should return false") {
  }
}
