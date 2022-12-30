package io.github.effiban.scala2java.spi.providers

import io.github.effiban.scala2java.spi.Scala2JavaExtension

/** A container for all extension provider hooks which are providers.
 *
 * @see [[Scala2JavaExtension]]
 */
trait ExtendedProviders {

  /** Override this method if you need to provide additional [[scala.meta.Importer]]-s (import statements) to the generated Java file.
   *
   * @return if overriden - a transformer which adds [[scala.meta.Importer]]-s<br>
   *         otherwise - the default transformer which does not add any<br>
   */
  def additionalImportersProvider(): AdditionalImportersProvider = AdditionalImportersProvider.Empty
}
