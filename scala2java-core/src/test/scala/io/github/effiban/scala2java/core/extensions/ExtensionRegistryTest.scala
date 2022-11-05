package io.github.effiban.scala2java.core.extensions

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.spi.Scala2JavaExtension
import io.github.effiban.scala2java.spi.providers.JavaImportersProvider

class ExtensionRegistryTest extends UnitTestSuite {

  private val extension1 = mock[Scala2JavaExtension]
  private val extension2 = mock[Scala2JavaExtension]

  private val importersProvider1 = mock[JavaImportersProvider]
  private val importersProvider2 = mock[JavaImportersProvider]

  test("javaImportersProviders") {
    when(extension1.javaImportersProvider()).thenReturn(importersProvider1)
    when(extension2.javaImportersProvider()).thenReturn(importersProvider2)

    val extensionRegistry = ExtensionRegistry(List(extension1, extension2))

    extensionRegistry.javaImportersProviders shouldBe List(importersProvider1, importersProvider2)
  }
}
