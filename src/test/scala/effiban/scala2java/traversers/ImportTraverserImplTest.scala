package effiban.scala2java.traversers

import effiban.scala2java.matchers.TreeMatcher.eqTree
import effiban.scala2java.stubbers.OutputWriterStubber.doWrite
import effiban.scala2java.testsuites.UnitTestSuite

import scala.meta.{Import, Importee, Importer, Name, Term}

class ImportTraverserImplTest extends UnitTestSuite {

  private val importerTraverser = mock[ImporterTraverser]

  private val importTraverser = new ImportTraverserImpl(importerTraverser)

  test("traverse") {
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

    importTraverser.traverse(Import(List(importer1, importer2)))

    outputWriter.toString shouldBe
      """import mypackage1.myclass1;
        |import mypackage2.myclass2;
        |""".stripMargin
  }
}
