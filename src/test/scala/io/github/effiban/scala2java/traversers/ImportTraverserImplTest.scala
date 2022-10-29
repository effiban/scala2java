package io.github.effiban.scala2java.traversers

import io.github.effiban.scala2java.contexts.StatContext
import io.github.effiban.scala2java.entities.JavaScope
import io.github.effiban.scala2java.matchers.TreeMatcher.eqTree
import io.github.effiban.scala2java.predicates.ImporterIncludedPredicate
import io.github.effiban.scala2java.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.testsuites.UnitTestSuite
import io.github.effiban.scala2java.testtrees.TermNames.Scala
import org.mockito.ArgumentMatchers.any

import scala.meta.{Import, Importee, Importer, Name, Term}

class ImportTraverserImplTest extends UnitTestSuite {

  private val PackageStatContext = StatContext(JavaScope.Package)

  private val importerTraverser = mock[ImporterTraverser]
  private val importerIncludedPredicate = mock[ImporterIncludedPredicate]

  private val importTraverser = new ImportTraverserImpl(importerTraverser, importerIncludedPredicate)

  test("traverse() in package scope when all importers should be included") {
    val importer1 = Importer(
      ref = Term.Name("mypackage1"),
      importees = List(Importee.Name(Name.Indeterminate("myclass1")))
    )
    val importer2 = Importer(
      ref = Term.Name("mypackage2"),
      importees = List(Importee.Name(Name.Indeterminate("myclass2")))
    )

    when(importerIncludedPredicate.apply(any[Importer])).thenAnswer( (importer: Importer) =>
      Seq(importer1, importer2).exists(_.structure == importer.structure)
    )

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

    when(importerIncludedPredicate.apply(any[Importer])).thenAnswer((importer: Importer) =>
      Seq(nonScalaImporter1, nonScalaImporter2).exists(_.structure == importer.structure)
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
