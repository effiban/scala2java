package io.github.effiban.scala2java.core.extensions

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.spi.Scala2JavaExtension
import io.github.effiban.scala2java.spi.providers.DefaultImportersProvider

class ExtensionRegistryTest extends UnitTestSuite {

  private val extension1 = mock[Scala2JavaExtension]
  private val extension2 = mock[Scala2JavaExtension]

  private val defaultImportersProvider1 = mock[DefaultImportersProvider]
  private val defaultImportersProvider2 = mock[DefaultImportersProvider]

  test("defaultImportersProviders") {
    when(extension1.defaultImportersProvider()).thenReturn(defaultImportersProvider1)
    when(extension2.defaultImportersProvider()).thenReturn(defaultImportersProvider2)

    val extensionRegistry = ExtensionRegistry(List(extension1, extension2))

    extensionRegistry.defaultImportersProviders shouldBe List(defaultImportersProvider1, defaultImportersProvider2)
  }
}
