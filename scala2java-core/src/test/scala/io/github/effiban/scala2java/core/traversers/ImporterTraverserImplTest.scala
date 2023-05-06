package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.{XtensionQuasiquoteImporter, XtensionQuasiquoteTerm}

class ImporterTraverserImplTest extends UnitTestSuite {

  private val defaultTermRefTraverser = mock[DefaultTermRefTraverser]

  private val importerTraverser = new ImporterTraverserImpl(defaultTermRefTraverser)


  test("traverse") {
    val termRef = q"mypackage"
    val traversedTermRef = q"mytraversedpackage"
    val importer = importer"mypackage.MyClass"
    val traversedImporter = importer"mytraversedpackage.MyClass"

    doReturn(traversedTermRef).when(defaultTermRefTraverser).traverse(eqTree(termRef))

    importerTraverser.traverse(importer).structure shouldBe traversedImporter.structure
  }
}
