package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchers.any

import scala.meta.{Import, Importer, XtensionQuasiquoteImporter}

class ImportTraverserImplTest extends UnitTestSuite {

  private val importerTraverser = mock[ImporterTraverser]

  private val importTraverser = new ImportTraverserImpl(importerTraverser)

  test("traverse() when importers have single importees") {
    val importer1 = importer"mypackage1.myclass1"
    val importer2 = importer"mypackage2.myclass2"
    val allImporters = List(importer1, importer2)

    val traversedImporter1 = importer"mypackage11.myclass11"
    val traversedImporter2 = importer"mypackage22.myclass22"
    val traversedImport = Import(List(traversedImporter1, traversedImporter2))

    doReturn(traversedImporter1).when(importerTraverser).traverse(eqTree(importer1))
    doReturn(traversedImporter2).when(importerTraverser).traverse(eqTree(importer2))

    importTraverser.traverse(Import(allImporters)).value.structure shouldBe traversedImport.structure
  }

  test("traverse() when importers have multiple importees") {
    val importer1 = importer"mypackage1.{myclass1, myclass2}"
    val importer2 = importer"mypackage2.{myclass3, myclass4}"
    val allImporters = List(importer1, importer2)

    val flattenedImporter1 = importer"mypackage1.myclass1"
    val flattenedImporter2 = importer"mypackage1.myclass2"
    val flattenedImporter3 = importer"mypackage2.myclass3"
    val flattenedImporter4 = importer"mypackage2.myclass4"

    val traversedImporter1 = importer"mypackage11.myclass11"
    val traversedImporter2 = importer"mypackage11.myclass22"
    val traversedImporter3 = importer"mypackage22.myclass33"
    val traversedImporter4 = importer"mypackage22.myclass44"
    val traversedImport = Import(List(traversedImporter1, traversedImporter2, traversedImporter3, traversedImporter4))

    doReturn(traversedImporter1).when(importerTraverser).traverse(eqTree(flattenedImporter1))
    doReturn(traversedImporter2).when(importerTraverser).traverse(eqTree(flattenedImporter2))
    doReturn(traversedImporter3).when(importerTraverser).traverse(eqTree(flattenedImporter3))
    doReturn(traversedImporter4).when(importerTraverser).traverse(eqTree(flattenedImporter4))

    importTraverser.traverse(Import(allImporters)).value.structure shouldBe traversedImport.structure
  }

  test("traverse() when some importers are duplicated should remove the duplicates") {
    val importer1A = importer"package1.myclass1"
    val importer1B = importer"package1.myclass1"
    val importer2 = importer"package2.myclass2"
    val importer3A = importer"package3.myclass3"
    val importer3B = importer"package3.myclass3"
    val inputImporters = List(
      importer1A,
      importer1B,
      importer2,
      importer3A,
      importer3B
    )
    val traversedImporter1 = importer"package11.myclass11"
    val traversedImporter2 = importer"package22.myclass22"
    val traversedImporter3 = importer"package33.myclass33"
    val traversedImport = Import(List(traversedImporter1, traversedImporter2, traversedImporter3))

    doReturn(traversedImporter1).when(importerTraverser).traverse(eqTree(importer1A))
    doReturn(traversedImporter2).when(importerTraverser).traverse(eqTree(importer2))
    doReturn(traversedImporter3).when(importerTraverser).traverse(eqTree(importer3A))

    importTraverser.traverse(Import(inputImporters)).value.structure shouldBe traversedImport.structure

    verify(importerTraverser, times(3)).traverse(any[Importer])
  }
}
