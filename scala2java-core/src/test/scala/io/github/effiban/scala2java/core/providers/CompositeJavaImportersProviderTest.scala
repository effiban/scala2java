package io.github.effiban.scala2java.core.providers

import io.github.effiban.scala2java.core.extensions.ExtensionRegistry
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.testtrees.TermNames
import io.github.effiban.scala2java.spi.providers.JavaImportersProvider

import scala.meta.Importee.Wildcard
import scala.meta.Term.Select
import scala.meta.{Importer, Term}

class CompositeJavaImportersProviderTest extends UnitTestSuite {

  private val DefaultImporter1 = Importer(Select(TermNames.Java, Term.Name("io")), List(Wildcard()))
  private val DefaultImporter2 = Importer(Select(TermNames.Java, Term.Name("lang")), List(Wildcard()))
  private val ExtraImporter1A = Importer(Select(Term.Name("aaa"), Term.Name("bbb")), List(Wildcard()))
  private val ExtraImporter1B = Importer(Select(Term.Name("ccc"), Term.Name("ddd")), List(Wildcard()))
  private val ExtraImporter2 = Importer(Select(Term.Name("eee"), Term.Name("fff")), List(Wildcard()))

  private val AllImporters = List(
    DefaultImporter1,
    DefaultImporter2,
    ExtraImporter1A,
    ExtraImporter1B,
    ExtraImporter2
  )

  private val defaultProvider = mock[JavaImportersProvider]
  private val extraProvider1 = mock[JavaImportersProvider]
  private val extraProvider2 = mock[JavaImportersProvider]
  private val extensionRegistry = mock[ExtensionRegistry]

  private val compositeProvider = new CompositeJavaImportersProvider(defaultProvider, extensionRegistry)

  test("provide") {
    when(extensionRegistry.javaImportersProviders).thenReturn(List(extraProvider1, extraProvider2))
    when(defaultProvider.provide()).thenReturn(List(DefaultImporter1, DefaultImporter2))
    when(extraProvider1.provide()).thenReturn(List(ExtraImporter1A, ExtraImporter1B))
    when(extraProvider2.provide()).thenReturn(List(ExtraImporter2))

    compositeProvider.provide().structure shouldBe AllImporters.structure
  }
}
