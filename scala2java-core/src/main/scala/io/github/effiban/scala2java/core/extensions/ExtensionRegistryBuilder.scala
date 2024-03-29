package io.github.effiban.scala2java.core.extensions

import io.github.effiban.scala2java.spi.Scala2JavaExtension

import java.util.ServiceLoader
import scala.jdk.StreamConverters._
import scala.meta.{Source, Term}

class ExtensionRegistryBuilder(forcedExtensionNamesResolver: ForcedExtensionNamesResolver,
                               extensionApplicablePredicate: ExtensionApplicablePredicate) {

  def buildFor(source: Source): ExtensionRegistry = {
    val termSelects = source
      .collect { case termSelect: Term.Select => termSelect }
      .distinctBy(_.structure)

    val forcedExtensionNames = forcedExtensionNamesResolver.resolve()

    val extensions = loadExtensions()
      .map(_.get)
      .filter(extension => extensionApplicablePredicate.apply(extension, forcedExtensionNames, termSelects))
      .toList
    ExtensionRegistry(extensions)
  }

  // Visible for testing
  private[extensions] def loadExtensions() = ServiceLoader.load(classOf[Scala2JavaExtension])
    .stream()
    .toScala(LazyList)
}

object ExtensionRegistryBuilder extends ExtensionRegistryBuilder(ForcedExtensionNamesResolver, ExtensionApplicablePredicate)
