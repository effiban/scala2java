package io.github.effiban.scala2java.traversers

import io.github.effiban.scala2java.classifiers.ImporterClassifier
import io.github.effiban.scala2java.contexts.StatContext
import io.github.effiban.scala2java.entities.JavaScope
import io.github.effiban.scala2java.matchers.TreeMatcher.eqTree
import io.github.effiban.scala2java.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.testsuites.UnitTestSuite
import io.github.effiban.scala2java.testtrees.TermNames.Scala
import org.mockito.ArgumentMatchers.any

import scala.meta.{Import, Importee, Importer, Name, Term}

class ImportTraverserImplTest extends UnitTestSuite {

  private val PackageStatContext = StatContext(JavaScope.Package)

  private val importerTraverser = mock[ImporterTraverser]
  private val importerClassifier = mock[ImporterClassifier]

  private val importTraverser = new ImportTraverserImpl(importerTraverser, importerClassifier)

  test("traverse() in package scope when there are no scala importers") {
    val importer1 = Importer(
      ref = Term.Name("mypackage1"),
      importees = List(Importee.Name(Name.Indeterminate("myclass1")))
    )
    val importer2 = Importer(
      ref = Term.Name("mypackage2"),
      importees = List(Importee.Name(Name.Indeterminate("myclass2")))
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

  test("traverse() in package scope when there are scala importers should skip them") {
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

    when(importerClassifier.isScala(any[Importer])).thenAnswer( (importer: Importer) => importer.ref match {
      case Term.Name("scala") => true
      case _ => false
    })

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