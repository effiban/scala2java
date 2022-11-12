package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.StatContext
import io.github.effiban.scala2java.core.entities.JavaScope
import io.github.effiban.scala2java.core.matchers.TreeMatcher.eqTree
import io.github.effiban.scala2java.core.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.testtrees.TermNames.Scala
import io.github.effiban.scala2java.spi.predicates.ImporterExcludedPredicate
import org.mockito.ArgumentMatchers.any

import scala.meta.{Import, Importee, Importer, Name, Term}

class ImportTraverserImplTest extends UnitTestSuite {

  private val PackageStatContext = StatContext(JavaScope.Package)

  private val importerTraverser = mock[ImporterTraverser]
  private val importerExcludedPredicate = mock[ImporterExcludedPredicate]

  private val importTraverser = new ImportTraverserImpl(importerTraverser, importerExcludedPredicate)

  test("traverse() in package scope when all importers should be included") {
    val importer1 = Importer(
      ref = Term.Name("mypackage1"),
      importees = List(Importee.Name(Name.Indeterminate("myclass1")))
    )
    val importer2 = Importer(
      ref = Term.Name("mypackage2"),
      importees = List(Importee.Name(Name.Indeterminate("myclass2")))
    )

    when(importerExcludedPredicate.apply(any[Importer])).thenReturn(false)

    doWrite("""import mypackage1.myclass1;
           |""".stripMargin)
      .when(importerTraverser).traverse(eqTree(importer1))
    doWrite("""import mypackage2.myclass2;
              |""".stripMargin)
      .when(importerTraverser).traverse(eqTree(importer2))

    importTraverser.traverse(`import` = Import(List(importer1, importer2)), context = PackageStatContext)

    outputWriter.toString shouldBe
      """import mypackage1.myclass1;
        |import mypackage2.myclass2;
        |""".stripMargin

    Seq(importer1, importer2)
      .foreach(importer => verify(importerExcludedPredicate).apply(eqTree(importer)))
  }

  test("traverse() in package scope when some importers should be excluded") {
    val scalaImporter1 = Importer(
      ref = Scala,
      importees = List(Importee.Name(Name.Indeterminate("myclass1")))
    )
    val nonScalaImporter1 = Importer(
      ref = Term.Name("mypackage1"),
      importees = List(Importee.Name(Name.Indeterminate("myclass1")))
    )
    val scalaImporter2 = Importer(
      ref = Scala,
      importees = List(Importee.Name(Name.Indeterminate("myclass2")))
    )
    val nonScalaImporter2 = Importer(
      ref = Term.Name("mypackage2"),
      importees = List(Importee.Name(Name.Indeterminate("myclass2")))
    )

    when(importerExcludedPredicate.apply(any[Importer])).thenAnswer((importer: Importer) =>
      Seq(scalaImporter1, scalaImporter2).exists(_.structure == importer.structure)
    )


    doWrite(
      """import mypackage1.myclass1;
        |""".stripMargin)
      .when(importerTraverser).traverse(eqTree(nonScalaImporter1))
    doWrite(
      """import mypackage2.myclass2;
        |""".stripMargin)
      .when(importerTraverser).traverse(eqTree(nonScalaImporter2))

    importTraverser.traverse(
      `import` = Import(List(scalaImporter1, nonScalaImporter1, scalaImporter2, nonScalaImporter2)),
      context = PackageStatContext
    )

    outputWriter.toString shouldBe
      """import mypackage1.myclass1;
        |import mypackage2.myclass2;
        |""".stripMargin
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
