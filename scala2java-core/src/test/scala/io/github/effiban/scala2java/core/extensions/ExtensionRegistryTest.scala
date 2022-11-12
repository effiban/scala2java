package io.github.effiban.scala2java.core.extensions

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.spi.Scala2JavaExtension
import io.github.effiban.scala2java.spi.providers.AdditionalImportersProvider

class ExtensionRegistryTest extends UnitTestSuite {

  private val extension1 = mock[Scala2JavaExtension]
  private val extension2 = mock[Scala2JavaExtension]

  private val additionalImportersProvider1 = mock[AdditionalImportersProvider]
  private val additionalImportersProvider2 = mock[AdditionalImportersProvider]

  test("additionalImportersProviders") {
    when(extension1.additionalImportersProvider()).thenReturn(additionalImportersProvider1)
    when(extension2.additionalImportersProvider()).thenReturn(additionalImportersProvider2)

    val extensionRegistry = ExtensionRegistry(List(extension1, extension2))

    extensionRegistry.additionalImportersProviders shouldBe List(additionalImportersProvider1, additionalImportersProvider2)
  }
}
