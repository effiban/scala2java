package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.StatContext
import io.github.effiban.scala2java.core.renderers.ImporterRenderer
import io.github.effiban.scala2java.core.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.spi.entities.JavaScope
import io.github.effiban.scala2java.spi.predicates.ImporterExcludedPredicate
import io.github.effiban.scala2java.spi.transformers.ImporterTransformer
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchers.any

import scala.meta.{Import, Importee, Importer, Name, Term, XtensionQuasiquoteImporter}

class ImportTraverserImplTest extends UnitTestSuite {

  private val PackageStatContext = StatContext(JavaScope.Package)

  private val importerTraverser = mock[ImporterTraverser]
  private val importerRenderer = mock[ImporterRenderer]
  private val importerExcludedPredicate = mock[ImporterExcludedPredicate]
  private val importerTransformer = mock[ImporterTransformer]

  private val importTraverser = new ImportTraverserImpl(
    importerTraverser,
    importerRenderer,
    importerExcludedPredicate,
    importerTransformer
  )

  test("traverse() in package scope when all should be included") {
    val importer1 = importer"mypackage1.myclass1"
    val importer2 = importer"mypackage2.myclass2"
    val allImporters = List(importer1, importer2)

    val traversedImporter1 = importer"mypackage11.myclass11"
    val traversedImporter2 = importer"mypackage22.myclass22"

    when(importerExcludedPredicate.apply(any[Importer])).thenReturn(false)
    when(importerTransformer.transform(any[Importer])).thenAnswer((importer: Importer) => importer)

    doReturn(traversedImporter1).when(importerTraverser).traverse(eqTree(importer1))
    doReturn(traversedImporter2).when(importerTraverser).traverse(eqTree(importer2))
    doWrite(
      """import mypackage11.myclass11;
        |""".stripMargin)
      .when(importerRenderer).render(eqTree(traversedImporter1))
    doWrite(
      """import mypackage22.myclass22;
        |""".stripMargin)
      .when(importerRenderer).render(eqTree(traversedImporter2))

    importTraverser.traverse(`import` = Import(allImporters), context = PackageStatContext)

    outputWriter.toString shouldBe
      """import mypackage11.myclass11;
        |import mypackage22.myclass22;
        |""".stripMargin

    Seq(importer1, importer2)
      .foreach(importer => verify(importerExcludedPredicate).apply(eqTree(importer)))
  }

  test("traverse() in package scope when all should be included and importers have multiple importees") {
    val importer1 = importer"mypackage1.{myclass1, myclass2}"
    val importer2 = importer"mypackage2.{myclass3, myclass4}"
    val allImporters = List(importer1, importer2)

    val flattenedImporter1 = importer"mypackage1.myclass1"
    val flattenedImporter2 = importer"mypackage1.myclass2"
    val flattenedImporter3 = importer"mypackage2.myclass3"
    val flattenedImporter4 = importer"mypackage2.myclass4"
    val allFlattenedImporters = List(flattenedImporter1, flattenedImporter2, flattenedImporter3, flattenedImporter4)

    val traversedFlattenedImporter1 = importer"mypackage11.myclass11"
    val traversedFlattenedImporter2 = importer"mypackage11.myclass22"
    val traversedFlattenedImporter3 = importer"mypackage22.myclass33"
    val traversedFlattenedImporter4 = importer"mypackage22.myclass44"

    when(importerExcludedPredicate.apply(any[Importer])).thenReturn(false)
    when(importerTransformer.transform(any[Importer])).thenAnswer((importer: Importer) => importer)

    doReturn(traversedFlattenedImporter1).when(importerTraverser).traverse(eqTree(flattenedImporter1))
    doReturn(traversedFlattenedImporter2).when(importerTraverser).traverse(eqTree(flattenedImporter2))
    doReturn(traversedFlattenedImporter3).when(importerTraverser).traverse(eqTree(flattenedImporter3))
    doReturn(traversedFlattenedImporter4).when(importerTraverser).traverse(eqTree(flattenedImporter4))

    doWrite(
      """import mypackage11.myclass11;
        |""".stripMargin)
      .when(importerRenderer).render(eqTree(traversedFlattenedImporter1))
    doWrite(
      """import mypackage11.myclass22;
        |""".stripMargin)
      .when(importerRenderer).render(eqTree(traversedFlattenedImporter2))
    doWrite(
      """import mypackage22.myclass33;
        |""".stripMargin)
      .when(importerRenderer).render(eqTree(traversedFlattenedImporter3))
    doWrite(
      """import mypackage22.myclass44;
        |""".stripMargin)
      .when(importerRenderer).render(eqTree(traversedFlattenedImporter4))

    importTraverser.traverse(`import` = Import(allImporters), context = PackageStatContext)

    outputWriter.toString shouldBe
      """import mypackage11.myclass11;
        |import mypackage11.myclass22;
        |import mypackage22.myclass33;
        |import mypackage22.myclass44;
        |""".stripMargin

    allFlattenedImporters.foreach(importer => verify(importerExcludedPredicate).apply(eqTree(importer)))
  }

  test("traverse() in package scope when some should be excluded") {
    val excludedImporter1 = importer"excluded1.myclass1"
    val excludedImporter2 = importer"excluded2.myclass2"
    val includedImporter1 = importer"included1.myclass1"
    val includedImporter2 = importer"included2.myclass2"
    val traversedIncludedImporter1 = importer"included11.myclass11"
    val traversedIncludedImporter2 = importer"included22.myclass22"
    val allImporters = List(excludedImporter1, includedImporter1, excludedImporter2, includedImporter2)

    when(importerExcludedPredicate.apply(any[Importer])).thenAnswer((importer: Importer) =>
      Seq(excludedImporter1, excludedImporter2).exists(_.structure == importer.structure)
    )
    when(importerTransformer.transform(any[Importer])).thenAnswer((importer: Importer) => importer)

    doReturn(traversedIncludedImporter1).when(importerTraverser).traverse(eqTree(includedImporter1))
    doReturn(traversedIncludedImporter2).when(importerTraverser).traverse(eqTree(includedImporter2))
    doWrite(
      """import included11.myclass11;
        |""".stripMargin)
      .when(importerRenderer).render(eqTree(traversedIncludedImporter1))
    doWrite(
      """import included22.myclass22;
        |""".stripMargin)
      .when(importerRenderer).render(eqTree(traversedIncludedImporter2))

    importTraverser.traverse(`import` = Import(allImporters), context = PackageStatContext)

    outputWriter.toString shouldBe
      """import included11.myclass11;
        |import included22.myclass22;
        |""".stripMargin
  }

  test("traverse() in package scope when some should be excluded and importers have multiple importees") {
    val importer1 = importer"mypackage1.{included1, excluded1}"
    val importer2 = importer"mypackage2.{included2, excluded2}"
    val allImporters = List(importer1, importer2)

    val includedFlattenedImporter1 = importer"mypackage1.included1"
    val excludedFlattenedImporter1 = importer"mypackage1.excluded1"
    val includedFlattenedImporter2 = importer"mypackage2.included2"
    val excludedFlattenedImporter2 = importer"mypackage2.excluded2"
    val traversedIncludedFlattenedImporter1 = importer"mypackage11.included11"
    val traversedIncludedFlattenedImporter2 = importer"mypackage22.included22"
    val allFlattenedImporters = List(
      includedFlattenedImporter1,
      excludedFlattenedImporter1,
      includedFlattenedImporter2,
      excludedFlattenedImporter2
    )

    when(importerExcludedPredicate.apply(any[Importer])).thenAnswer((importer: Importer) =>
      Seq(excludedFlattenedImporter1, excludedFlattenedImporter2).exists(_.structure == importer.structure)
    )
    when(importerTransformer.transform(any[Importer])).thenAnswer((importer: Importer) => importer)

    doReturn(traversedIncludedFlattenedImporter1).when(importerTraverser).traverse(eqTree(includedFlattenedImporter1))
    doReturn(traversedIncludedFlattenedImporter2).when(importerTraverser).traverse(eqTree(includedFlattenedImporter2))
    doWrite(
      """import mypackage11.included11;
        |""".stripMargin)
      .when(importerRenderer).render(eqTree(traversedIncludedFlattenedImporter1))
    doWrite(
      """import mypackage22.included22;
        |""".stripMargin)
      .when(importerRenderer).render(eqTree(traversedIncludedFlattenedImporter2))

    importTraverser.traverse(`import` = Import(allImporters), context = PackageStatContext)

    outputWriter.toString shouldBe
      """import mypackage11.included11;
        |import mypackage22.included22;
        |""".stripMargin

    allFlattenedImporters.foreach(importer => verify(importerExcludedPredicate).apply(eqTree(importer)))
  }

  test("traverse() in package scope when importers should be transformed") {
    val importer1A = importer"package1A.myclass1A"
    val importer1B = importer"package1B.myclass1B"
    val importer2A = importer"package2A.myclass2A"
    val importer2B = importer"package2B.myclass2B"
    val traversedImporter1B = importer"package11B.myclass11B"
    val traversedImporter2B = importer"package22B.myclass22B"
    val inputImporters = List(
      importer1A,
      importer2A
    )

    when(importerTransformer.transform(any[Importer])).thenAnswer((importer: Importer) =>
      importer match {
        case anImporter if anImporter.structure == importer1A.structure => importer1B
        case anImporter if anImporter.structure == importer2A.structure => importer2B
      }
    )

    doReturn(traversedImporter1B).when(importerTraverser).traverse(eqTree(importer1B))
    doReturn(traversedImporter2B).when(importerTraverser).traverse(eqTree(importer2B))

    doWrite(
      """import package11B.myclass11B;
        |""".stripMargin)
      .when(importerRenderer).render(eqTree(traversedImporter1B))
    doWrite(
      """import package22B.myclass22B;
        |""".stripMargin)
      .when(importerRenderer).render(eqTree(traversedImporter2B))

    importTraverser.traverse(`import` = Import(inputImporters), context = PackageStatContext)

    outputWriter.toString shouldBe
      """import package11B.myclass11B;
        |import package22B.myclass22B;
        |""".stripMargin
  }

  test("traverse() in package scope when some importers are duplicated should remove the duplicates") {
    val importer1A = importer"package1.myclass1"
    val importer1B = importer"package1.myclass1"
    val importer2 = importer"package2.myclass2"
    val importer3A = importer"package3.myclass3"
    val importer3B = importer"package3.myclass3"
    val traversedImporter1 = importer"package11.myclass11"
    val traversedImporter2 = importer"package22.myclass22"
    val traversedImporter3 = importer"package33.myclass33"
    val inputImporters = List(
      importer1A,
      importer1B,
      importer2,
      importer3A,
      importer3B
    )

    when(importerTransformer.transform(any[Importer])).thenAnswer((importer: Importer) => importer)

    doReturn(traversedImporter1).when(importerTraverser).traverse(eqTree(importer1A))
    doReturn(traversedImporter2).when(importerTraverser).traverse(eqTree(importer2))
    doReturn(traversedImporter3).when(importerTraverser).traverse(eqTree(importer3A))
    doWrite(
      """import package11.myclass11;
        |""".stripMargin)
      .when(importerRenderer).render(eqTree(traversedImporter1))
    doWrite(
      """import package22.myclass22;
        |""".stripMargin)
      .when(importerRenderer).render(eqTree(traversedImporter2))
    doWrite(
      """import package33.myclass33;
        |""".stripMargin)
      .when(importerRenderer).render(eqTree(traversedImporter3))

    importTraverser.traverse(`import` = Import(inputImporters), context = PackageStatContext)

    outputWriter.toString shouldBe
      """import package11.myclass11;
        |import package22.myclass22;
        |import package33.myclass33;
        |""".stripMargin

    verify(importerTraverser, times(3)).traverse(any[Importer])
  }

  test("traverse() in class scope should write a comment") {
    val importer = Importer(
      ref = Term.Name("mypackage1"),
      importees = List(Importee.Name(Name.Indeterminate("myclass1")))
    )

    importTraverser.traverse(`import` = Import(List(importer)), context = StatContext(JavaScope.Class))

    outputWriter.toString shouldBe "/* import mypackage1.myclass1 */"
  }
}
