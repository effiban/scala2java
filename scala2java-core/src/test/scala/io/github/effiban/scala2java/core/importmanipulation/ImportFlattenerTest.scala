package io.github.effiban.scala2java.core.importmanipulation

import io.github.effiban.scala2java.core.importmanipulation.ImportFlattener.flatten
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite

import scala.meta.{XtensionQuasiquoteImporter, XtensionQuasiquoteTerm}

class ImportFlattenerTest extends UnitTestSuite {

  test("flatten when empty should return empty") {
    flatten(Nil) shouldBe Nil
  }

  test("flatten for: one import, one importer, one importee") {
    flatten(List(q"import a.b")).structure shouldBe List(importer"a.b").structure
  }

  test("flatten for: one import, one importer, two importees") {
    flatten(List(q"import a.{b, c}")).structure shouldBe List(importer"a.b", importer"a.c").structure
  }

  test("flatten for: one import, two importers, two importees each, all have names") {
    val `import` = q"import a.{b, c}, d.{e, f}"
    val expectedImporters = List(
      importer"a.b",
      importer"a.c",
      importer"d.e",
      importer"d.f"
    )
    flatten(List(`import`)).structure shouldBe expectedImporters.structure
  }

  test("flatten for: one import, two importers, with mixture of names and wildcards") {
    val `import` = q"import a.{b, c}, d._"
    val expectedImporters = List(
      importer"a.b",
      importer"a.c",
      importer"d._"
    )
    flatten(List(`import`)).structure shouldBe expectedImporters.structure
  }

  test("flatten for two imports, two importers each, two importees each") {
    val imports = List(
      q"import a.{b, c}, d.{e, f}",
      q"import g.{h, i}, j.{k, l}"
    )

    val expectedImporters = List(
      importer"a.b",
      importer"a.c",
      importer"d.e",
      importer"d.f",
      importer"g.h",
      importer"g.i",
      importer"j.k",
      importer"j.l",
    )
    flatten(imports).structure shouldBe expectedImporters.structure
  }
}
