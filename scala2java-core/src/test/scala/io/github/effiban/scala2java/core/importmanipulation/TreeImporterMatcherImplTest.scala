package io.github.effiban.scala2java.core.importmanipulation

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.{XtensionQuasiquoteImporter, XtensionQuasiquoteType}

class TreeImporterMatcherImplTest extends UnitTestSuite {

  private val typeSelectImporterMatcher = mock[TypeSelectImporterMatcher]

  private val treeImporterMatcher = new TreeImporterMatcherImpl(typeSelectImporterMatcher)

  test("matches() for Type.Select when inner matcher returns true") {
    val typeSelect = t"a.b.C"
    val importer = importer"a.b.C"

    doReturn(true).when(typeSelectImporterMatcher).matches(eqTree(typeSelect), eqTree(importer))

    treeImporterMatcher.matches(typeSelect, importer) shouldBe true
  }

  test("matches() for Type.Select when inner matcher returns false") {
    val typeSelect = t"a.b.C"
    val importer = importer"a.b.D"

    doReturn(false).when(typeSelectImporterMatcher).matches(eqTree(typeSelect), eqTree(importer))

    treeImporterMatcher.matches(typeSelect, importer) shouldBe false
  }
}
