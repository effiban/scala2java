package io.github.effiban.scala2java.core.extensions

import io.github.effiban.scala2java.spi.Scala2JavaExtension

import java.util.ServiceLoader
import scala.jdk.StreamConverters._
import scala.meta.Source

class ExtensionRegistryBuilder {

  def buildFor(source: Source): ExtensionRegistry = {
    val extensions = loadExtensions()
      .map(_.get)
      .filter(_.shouldBeAppliedTo(source))
      .toList
    ExtensionRegistry(extensions)
  }

  // Visible for testing
  private[extensions] def loadExtensions() = ServiceLoader.load(classOf[Scala2JavaExtension])
    .stream()
    .toScala(LazyList)
}

object ExtensionRegistryBuilder extends ExtensionRegistryBuilder
