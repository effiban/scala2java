package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.spi.predicates.ImporterExcludedPredicate
import io.github.effiban.scala2java.spi.transformers.ImporterTransformer
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchers.any

import scala.meta.{Import, Importer, XtensionQuasiquoteImporter}

class ImportTraverserImplTest extends UnitTestSuite {

  private val importerTraverser = mock[ImporterTraverser]
  private val importerExcludedPredicate = mock[ImporterExcludedPredicate]
  private val importerTransformer = mock[ImporterTransformer]

  private val importTraverser = new ImportTraverserImpl(
    importerTraverser,
    importerExcludedPredicate,
    importerTransformer
  )

  test("traverse() when all should be included") {
    val importer1 = importer"mypackage1.myclass1"
    val importer2 = importer"mypackage2.myclass2"
    val allImporters = List(importer1, importer2)

    val traversedImporter1 = importer"mypackage11.myclass11"
    val traversedImporter2 = importer"mypackage22.myclass22"
    val traversedImport = Import(List(traversedImporter1, traversedImporter2))

    when(importerExcludedPredicate.apply(any[Importer])).thenReturn(false)
    when(importerTransformer.transform(any[Importer])).thenAnswer((importer: Importer) => importer)

    doReturn(traversedImporter1).when(importerTraverser).traverse(eqTree(importer1))
    doReturn(traversedImporter2).when(importerTraverser).traverse(eqTree(importer2))

    importTraverser.traverse(Import(allImporters)).value.structure shouldBe traversedImport.structure

    Seq(importer1, importer2)
      .foreach(importer => verify(importerExcludedPredicate).apply(eqTree(importer)))
  }

  test("traverse() when all should be included and importers have multiple importees") {
    val importer1 = importer"mypackage1.{myclass1, myclass2}"
    val importer2 = importer"mypackage2.{myclass3, myclass4}"
    val allImporters = List(importer1, importer2)

    val flattenedImporter1 = importer"mypackage1.myclass1"
    val flattenedImporter2 = importer"mypackage1.myclass2"
    val flattenedImporter3 = importer"mypackage2.myclass3"
    val flattenedImporter4 = importer"mypackage2.myclass4"
    val allFlattenedImporters = List(flattenedImporter1, flattenedImporter2, flattenedImporter3, flattenedImporter4)

    val traversedImporter1 = importer"mypackage11.myclass11"
    val traversedImporter2 = importer"mypackage11.myclass22"
    val traversedImporter3 = importer"mypackage22.myclass33"
    val traversedImporter4 = importer"mypackage22.myclass44"
    val traversedImport = Import(List(traversedImporter1, traversedImporter2, traversedImporter3, traversedImporter4))

    when(importerExcludedPredicate.apply(any[Importer])).thenReturn(false)
    when(importerTransformer.transform(any[Importer])).thenAnswer((importer: Importer) => importer)

    doReturn(traversedImporter1).when(importerTraverser).traverse(eqTree(flattenedImporter1))
    doReturn(traversedImporter2).when(importerTraverser).traverse(eqTree(flattenedImporter2))
    doReturn(traversedImporter3).when(importerTraverser).traverse(eqTree(flattenedImporter3))
    doReturn(traversedImporter4).when(importerTraverser).traverse(eqTree(flattenedImporter4))

    importTraverser.traverse(Import(allImporters)).value.structure shouldBe traversedImport.structure

    allFlattenedImporters.foreach(importer => verify(importerExcludedPredicate).apply(eqTree(importer)))
  }

  test("traverse() when some should be excluded") {
    val excludedImporter1 = importer"excluded1.myclass1"
    val excludedImporter2 = importer"excluded2.myclass2"
    val includedImporter1 = importer"included1.myclass1"
    val includedImporter2 = importer"included2.myclass2"
    val allImporters = List(excludedImporter1, includedImporter1, excludedImporter2, includedImporter2)

    val traversedImporter1 = importer"included11.myclass11"
    val traversedImporter2 = importer"included22.myclass22"
    val traversedImport = Import(List(traversedImporter1, traversedImporter2))

    when(importerExcludedPredicate.apply(any[Importer])).thenAnswer((importer: Importer) =>
      Seq(excludedImporter1, excludedImporter2).exists(_.structure == importer.structure)
    )
    when(importerTransformer.transform(any[Importer])).thenAnswer((importer: Importer) => importer)

    doReturn(traversedImporter1).when(importerTraverser).traverse(eqTree(includedImporter1))
    doReturn(traversedImporter2).when(importerTraverser).traverse(eqTree(includedImporter2))

    importTraverser.traverse(Import(allImporters)).value.structure shouldBe traversedImport.structure
  }

  test("traverse() when some should be excluded and importers have multiple importees") {
    val importer1 = importer"mypackage1.{included1, excluded1}"
    val importer2 = importer"mypackage2.{included2, excluded2}"
    val allImporters = List(importer1, importer2)

    val includedFlattenedImporter1 = importer"mypackage1.included1"
    val excludedFlattenedImporter1 = importer"mypackage1.excluded1"
    val includedFlattenedImporter2 = importer"mypackage2.included2"
    val excludedFlattenedImporter2 = importer"mypackage2.excluded2"
    val allFlattenedImporters = List(
      includedFlattenedImporter1,
      excludedFlattenedImporter1,
      includedFlattenedImporter2,
      excludedFlattenedImporter2
    )
    val traversedImporter1 = importer"mypackage11.included11"
    val traversedImporter2 = importer"mypackage22.included22"
    val traversedImport = Import(List(traversedImporter1, traversedImporter2))

    when(importerExcludedPredicate.apply(any[Importer])).thenAnswer((importer: Importer) =>
      Seq(excludedFlattenedImporter1, excludedFlattenedImporter2).exists(_.structure == importer.structure)
    )
    when(importerTransformer.transform(any[Importer])).thenAnswer((importer: Importer) => importer)

    doReturn(traversedImporter1).when(importerTraverser).traverse(eqTree(includedFlattenedImporter1))
    doReturn(traversedImporter2).when(importerTraverser).traverse(eqTree(includedFlattenedImporter2))

    importTraverser.traverse(Import(allImporters)).value.structure shouldBe traversedImport.structure

    allFlattenedImporters.foreach(importer => verify(importerExcludedPredicate).apply(eqTree(importer)))
  }

  test("traverse() when all should be excluded") {
    val importer1 = importer"mypackage1.class1"
    val importer2 = importer"mypackage2.class2"
    val allImporters = List(importer1, importer2)

    when(importerExcludedPredicate.apply(any[Importer])).thenReturn(true)

    importTraverser.traverse(Import(allImporters)) shouldBe None
  }

  test("traverse() when importers should be transformed") {
    val importer1A = importer"package1A.myclass1A"
    val importer1B = importer"package1B.myclass1B"
    val importer2A = importer"package2A.myclass2A"
    val importer2B = importer"package2B.myclass2B"
    val inputImporters = List(
      importer1A,
      importer2A
    )
    val traversedImporter1B = importer"package11B.myclass11B"
    val traversedImporter2B = importer"package22B.myclass22B"
    val traversedImport = Import(List(traversedImporter1B, traversedImporter2B))

    when(importerTransformer.transform(any[Importer])).thenAnswer((importer: Importer) =>
      importer match {
        case anImporter if anImporter.structure == importer1A.structure => importer1B
        case anImporter if anImporter.structure == importer2A.structure => importer2B
      }
    )

    doReturn(traversedImporter1B).when(importerTraverser).traverse(eqTree(importer1B))
    doReturn(traversedImporter2B).when(importerTraverser).traverse(eqTree(importer2B))

    importTraverser.traverse(Import(inputImporters)).value.structure shouldBe traversedImport.structure
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

    when(importerTransformer.transform(any[Importer])).thenAnswer((importer: Importer) => importer)

    doReturn(traversedImporter1).when(importerTraverser).traverse(eqTree(importer1A))
    doReturn(traversedImporter2).when(importerTraverser).traverse(eqTree(importer2))
    doReturn(traversedImporter3).when(importerTraverser).traverse(eqTree(importer3A))

    importTraverser.traverse(Import(inputImporters)).value.structure shouldBe traversedImport.structure

    verify(importerTraverser, times(3)).traverse(any[Importer])
  }
}
