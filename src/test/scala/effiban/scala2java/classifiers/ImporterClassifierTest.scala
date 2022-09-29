package effiban.scala2java.classifiers

import effiban.scala2java.testsuites.UnitTestSuite
import effiban.scala2java.testtrees.TermNames.Scala

import scala.meta.{Importee, Importer, Name, Term}

class ImporterClassifierTest extends UnitTestSuite {

  private val Importees = List(Importee.Name(Name.Indeterminate("myclass1")))

  test("isScala() when ref is just 'scala' should return true") {
    val importer = Importer(ref = Scala, importees = Importees)
    ImporterClassifier.isScala(importer) shouldBe true
  }

  test("isScala() when ref has two parts starting with 'scala' should return true") {
    val ref = Term.Select(Scala, Term.Name("pkg"))
    val importer = Importer(ref = ref, importees = Importees)
    ImporterClassifier.isScala(importer) shouldBe true
  }

  test("isScala() when ref has three parts starting with 'scala' should return true") {
    val ref = Term.Select(Term.Select(Scala, Term.Name("pkg1")), Term.Name("pkg2"))
    val importer = Importer(ref = ref, importees = Importees)
    ImporterClassifier.isScala(importer) shouldBe true
  }

  test("isScala() when ref has three parts with 'scala' in the middle should return true") {
    val ref = Term.Select(Term.Select(Term.Name("pkg1"), Scala), Term.Name("pkg2"))
    val importer = Importer(ref = ref, importees = Importees)
    ImporterClassifier.isScala(importer) shouldBe true
  }

  test("isScala() when ref has three parts with 'scala' at the end should return true") {
    val ref = Term.Select(Term.Select(Term.Name("pkg1"), Term.Name("pkg2")), Scala)
    val importer = Importer(ref = ref, importees = Importees)
    ImporterClassifier.isScala(importer) shouldBe true
  }

  test("isScala() when ref has one part which is not 'scala' should return false") {
    val importer = Importer(ref = Term.Name("pkg"), importees = Importees)
    ImporterClassifier.isScala(importer) shouldBe false
  }

  test("isScala() when ref has two parts, none of which are 'scala' should return false") {
    val importer = Importer(ref = Term.Select(Term.Name("pkg1"), Term.Name("pkg2")), importees = Importees)
    ImporterClassifier.isScala(importer) shouldBe false
  }

  test("isScala() when ref has three parts, none of which are 'scala' should return false") {
    val importer = Importer(ref = Term.Select(Term.Select(Term.Name("pkg1"), Term.Name("pkg2")), Term.Name("pkg3")), importees = Importees)
    ImporterClassifier.isScala(importer) shouldBe false
  }
}
