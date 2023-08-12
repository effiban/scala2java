package io.github.effiban.scala2java.core.importeradders

import io.github.effiban.scala2java.core.importeradders.ImporterDeduplicater.dedup
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite

import scala.meta.XtensionQuasiquoteImporter

class ImporterDeduplicaterTest extends UnitTestSuite {

  test("resolve when all have names, no duplicates") {
    val importer1 = importer"a.b.c"
    val importer2 = importer"a.b.d"
    val importers = List(importer1, importer2)

    dedup(importers).structure shouldBe importers.structure
  }

  test("resolve when all have names, and include duplicate names") {
    val importer1 = importer"a.b.c"
    val importer2 = importer"a.b.d"
    val importer3 = importer"e.f.d"
    val inputImporters = List(importer1, importer2, importer3)
    val expectedImporters = List(importer1, importer2)

    dedup(inputImporters).structure shouldBe expectedImporters.structure
  }

  test("resolve when all have wildcards, no duplicates") {
    val importer1 = importer"a.b._"
    val importer2 = importer"a.c._"
    val importers = List(importer1, importer2)

    dedup(importers).structure shouldBe importers.structure
  }

  test("resolve when all have wildcards, and include duplicates") {
    val importer1 = importer"a.b._"
    val importer2 = importer"a.c._"
    val importer3 = importer"a.b._"
    val inputImporters = List(importer1, importer2, importer3)
    val expectedImporters = List(importer1, importer2)

    dedup(inputImporters).structure shouldBe expectedImporters.structure
  }

  test("resolve when have names and wildcards, no duplicates") {
    val importer1 = importer"a.b._"
    val importer2 = importer"c.d.E"
    val importers = List(importer1, importer2)

    dedup(importers).structure shouldBe importers.structure
  }

  test("resolve when have names and wildcards, and duplicates of both types") {
    val importer1 = importer"a.b._"
    val importer2 = importer"c.d.E"
    val importer3 = importer"a.b._"
    val importer4 = importer"f.g.E"
    val inputImporters = List(importer1, importer2, importer3, importer4)
    val expectedImporters = List(importer1, importer2)

    dedup(inputImporters).structure shouldBe expectedImporters.structure
  }

  test("resolve when empty should return empty") {
    dedup(Nil) shouldBe Nil
  }
}
