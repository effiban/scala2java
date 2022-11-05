package io.github.effiban.scala2java.spi

import io.github.effiban.scala2java.spi.providers.JavaImportersProvider

trait Scala2JavaExtension {

  def javaImportersProvider(): JavaImportersProvider = JavaImportersProvider.Empty
}
