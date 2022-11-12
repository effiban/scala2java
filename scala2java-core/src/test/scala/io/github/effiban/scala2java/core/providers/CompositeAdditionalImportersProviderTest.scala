package io.github.effiban.scala2java.core.providers

import io.github.effiban.scala2java.core.extensions.ExtensionRegistry
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.testtrees.TermNames
import io.github.effiban.scala2java.spi.providers.AdditionalImportersProvider

import scala.meta.Importee.Wildcard
import scala.meta.Term.Select
import scala.meta.{Importer, Term}

class CompositeAdditionalImportersProviderTest extends UnitTestSuite {

  private val CoreImporter1 = Importer(Select(TermNames.Java, Term.Name("io")), List(Wildcard()))
  private val CoreImporter2 = Importer(Select(TermNames.Java, Term.Name("lang")), List(Wildcard()))
  private val ExtraImporter1A = Importer(Select(Term.Name("aaa"), Term.Name("bbb")), List(Wildcard()))
  private val ExtraImporter1B = Importer(Select(Term.Name("ccc"), Term.Name("ddd")), List(Wildcard()))
  private val ExtraImporter2 = Importer(Select(Term.Name("eee"), Term.Name("fff")), List(Wildcard()))

  private val AllImporters = List(
    CoreImporter1,
    CoreImporter2,
    ExtraImporter1A,
    ExtraImporter1B,
    ExtraImporter2
  )

  private val coreProvider = mock[AdditionalImportersProvider]
  private val extraProvider1 = mock[AdditionalImportersProvider]
  private val extraProvider2 = mock[AdditionalImportersProvider]
  private implicit val extensionRegistry: ExtensionRegistry = mock[ExtensionRegistry]

  private val compositeProvider = new CompositeAdditionalImportersProvider(coreProvider)

  test("provide") {
    when(extensionRegistry.additionalImportersProviders).thenReturn(List(extraProvider1, extraProvider2))
    when(coreProvider.provide()).thenReturn(List(CoreImporter1, CoreImporter2))
    when(extraProvider1.provide()).thenReturn(List(ExtraImporter1A, ExtraImporter1B))
    when(extraProvider2.provide()).thenReturn(List(ExtraImporter2))

    compositeProvider.provide().structure shouldBe AllImporters.structure
  }
}
