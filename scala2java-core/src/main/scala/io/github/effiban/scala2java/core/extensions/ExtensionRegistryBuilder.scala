package io.github.effiban.scala2java.core.extensions

import io.github.effiban.scala2java.spi.Scala2JavaExtension

import java.util.ServiceLoader
import scala.jdk.StreamConverters._
import scala.meta.{Source, Term}

class ExtensionRegistryBuilder {

  def buildFor(source: Source): ExtensionRegistry = {
    val termSelects = source
      .collect { case termSelect: Term.Select => termSelect }
      .distinctBy(_.structure)

    val extensions = loadExtensions()
      .map(_.get)
      .filter(extension => shouldBeAppliedForAnyOf(extension, termSelects))
      .toList
    ExtensionRegistry(extensions)
  }

  // Visible for testing
  private[extensions] def loadExtensions() = ServiceLoader.load(classOf[Scala2JavaExtension])
    .stream()
    .toScala(LazyList)

  private def shouldBeAppliedForAnyOf(extension: Scala2JavaExtension, termSelects: List[Term.Select]) = termSelects.exists(extension.shouldBeAppliedIfContains)
}

object ExtensionRegistryBuilder extends ExtensionRegistryBuilder
