package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.StatContext
import io.github.effiban.scala2java.core.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.spi.entities.JavaScope
import io.github.effiban.scala2java.spi.predicates.ImporterExcludedPredicate
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchers.any

import scala.meta.{Import, Importee, Importer, Name, Term, XtensionQuasiquoteImporter}

class ImportTraverserImplTest extends UnitTestSuite {

  private val PackageStatContext = StatContext(JavaScope.Package)

  private val importerTraverser = mock[ImporterTraverser]
  private val importerExcludedPredicate = mock[ImporterExcludedPredicate]

  private val importTraverser = new ImportTraverserImpl(importerTraverser, importerExcludedPredicate)

  test("traverse() in package scope when all should be included") {
    val importer1 = importer"mypackage1.myclass1"
    val importer2 = importer"mypackage2.myclass2"
    val allImporters = List(importer1, importer2)

    when(importerExcludedPredicate.apply(any[Importer])).thenReturn(false)

    doWrite("""import mypackage1.myclass1;
           |""".stripMargin)
      .when(importerTraverser).traverse(eqTree(importer1))
    doWrite("""import mypackage2.myclass2;
              |""".stripMargin)
      .when(importerTraverser).traverse(eqTree(importer2))

    importTraverser.traverse(`import` = Import(allImporters), context = PackageStatContext)

    outputWriter.toString shouldBe
      """import mypackage1.myclass1;
        |import mypackage2.myclass2;
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

    when(importerExcludedPredicate.apply(any[Importer])).thenReturn(false)

    doWrite(
      """import mypackage1.myclass1;
        |""".stripMargin)
      .when(importerTraverser).traverse(eqTree(flattenedImporter1))
    doWrite(
      """import mypackage1.myclass2;
        |""".stripMargin)
      .when(importerTraverser).traverse(eqTree(flattenedImporter2))
    doWrite(
      """import mypackage2.myclass3;
        |""".stripMargin)
      .when(importerTraverser).traverse(eqTree(flattenedImporter3))
    doWrite(
      """import mypackage2.myclass4;
        |""".stripMargin)
      .when(importerTraverser).traverse(eqTree(flattenedImporter4))

    importTraverser.traverse(`import` = Import(allImporters), context = PackageStatContext)

    outputWriter.toString shouldBe
      """import mypackage1.myclass1;
        |import mypackage1.myclass2;
        |import mypackage2.myclass3;
        |import mypackage2.myclass4;
        |""".stripMargin

    allFlattenedImporters.foreach(importer => verify(importerExcludedPredicate).apply(eqTree(importer)))
  }

  test("traverse() in package scope when some should be excluded") {
    val excludedImporter1 = importer"excluded1.myclass1"
    val excludedImporter2 = importer"excluded2.myclass2"
    val includedImporter1 = importer"included1.myclass1"
    val includedImporter2 = importer"included2.myclass2"
    val allImporters = List(excludedImporter1, includedImporter1, excludedImporter2, includedImporter2)

    when(importerExcludedPredicate.apply(any[Importer])).thenAnswer((importer: Importer) =>
      Seq(excludedImporter1, excludedImporter2).exists(_.structure == importer.structure)
    )


    doWrite(
      """import included1.myclass1;
        |""".stripMargin)
      .when(importerTraverser).traverse(eqTree(includedImporter1))
    doWrite(
      """import included2.myclass2;
        |""".stripMargin)
      .when(importerTraverser).traverse(eqTree(includedImporter2))

    importTraverser.traverse(`import` = Import(allImporters), context = PackageStatContext)

    outputWriter.toString shouldBe
      """import included1.myclass1;
        |import included2.myclass2;
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
    val allFlattenedImporters = List(
      includedFlattenedImporter1,
      excludedFlattenedImporter1,
      includedFlattenedImporter2,
      excludedFlattenedImporter2
    )

    when(importerExcludedPredicate.apply(any[Importer])).thenAnswer((importer: Importer) =>
      Seq(excludedFlattenedImporter1, excludedFlattenedImporter2).exists(_.structure == importer.structure)
    )

    doWrite(
      """import mypackage1.included1;
        |""".stripMargin)
      .when(importerTraverser).traverse(eqTree(includedFlattenedImporter1))
    doWrite(
      """import mypackage2.included2;
        |""".stripMargin)
      .when(importerTraverser).traverse(eqTree(includedFlattenedImporter2))

    importTraverser.traverse(`import` = Import(allImporters), context = PackageStatContext)

    outputWriter.toString shouldBe
      """import mypackage1.included1;
        |import mypackage2.included2;
        |""".stripMargin

    allFlattenedImporters.foreach(importer => verify(importerExcludedPredicate).apply(eqTree(importer)))
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
