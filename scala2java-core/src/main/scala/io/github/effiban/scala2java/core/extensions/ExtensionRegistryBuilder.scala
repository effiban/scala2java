package io.github.effiban.scala2java.core.extensions

import io.github.effiban.scala2java.spi.Scala2JavaExtension

import java.util.ServiceLoader
import scala.jdk.CollectionConverters._

object ExtensionRegistryBuilder {

  def build(): ExtensionRegistry = {
    val extensions = ServiceLoader.load(classOf[Scala2JavaExtension])
      .iterator
      .asScala
      .toList
    ExtensionRegistry(extensions)
  }
}
