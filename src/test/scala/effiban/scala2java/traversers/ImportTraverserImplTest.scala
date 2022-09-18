package effiban.scala2java.traversers

import effiban.scala2java.classifiers.ImporterClassifier
import effiban.scala2java.contexts.StatContext
import effiban.scala2java.entities.JavaScope
import effiban.scala2java.matchers.TreeMatcher.eqTree
import effiban.scala2java.stubbers.OutputWriterStubber.doWrite
import effiban.scala2java.testsuites.UnitTestSuite
import effiban.scala2java.testtrees.TermNames.ScalaTermName
import org.mockito.ArgumentMatchers.any

import scala.meta.{Import, Importee, Importer, Name, Term}

class ImportTraverserImplTest extends UnitTestSuite {

  private val TheStatContext = StatContext(JavaScope.Package)

  private val importerTraverser = mock[ImporterTraverser]
  private val importerClassifier = mock[ImporterClassifier]

  private val importTraverser = new ImportTraverserImpl(importerTraverser, importerClassifier)

  test("traverse() when there are no scala importers") {
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

    importTraverser.traverse(`import` = Import(List(importer1, importer2)), context = TheStatContext)

    outputWriter.toString shouldBe
      """import mypackage1.myclass1;
        |import mypackage2.myclass2;
        |""".stripMargin
  }

  test("traverse() when there are scala importers should skip them") {
    val scalaImporter1 = Importer(
      ref = ScalaTermName,
      importees = List(Importee.Name(Name.Indeterminate("myclass1")))
    )
    val nonScalaImporter1 = Importer(
      ref = Term.Name("mypackage1"),
      importees = List(Importee.Name(Name.Indeterminate("myclass1")))
    )
    val scalaImporter2 = Importer(
      ref = ScalaTermName,
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
      context = TheStatContext
    )

    outputWriter.toString shouldBe
      """import mypackage1.myclass1;
        |import mypackage2.myclass2;
        |""".stripMargin
  }
}
