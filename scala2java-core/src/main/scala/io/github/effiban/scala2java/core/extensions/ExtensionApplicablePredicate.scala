package io.github.effiban.scala2java.core.extensions

import io.github.effiban.scala2java.spi.Scala2JavaExtension

import scala.meta.Term

private[extensions] trait ExtensionApplicablePredicate {
  def apply(extension: Scala2JavaExtension,
            forcedExtensionNames: Iterable[String] = Nil,
            termSelects: Iterable[Term.Select] = Nil): Boolean
}

private[extensions] object ExtensionApplicablePredicate extends ExtensionApplicablePredicate {

  override def apply(extension: Scala2JavaExtension,
                     forcedExtensionNames: Iterable[String] = Nil,
                     termSelects: Iterable[Term.Select] = Nil): Boolean = {
    forcedExtensionNames.exists(extension.getClass.getCanonicalName.contains) ||
      termSelects.exists(extension.shouldBeAppliedIfContains)
  }

}
