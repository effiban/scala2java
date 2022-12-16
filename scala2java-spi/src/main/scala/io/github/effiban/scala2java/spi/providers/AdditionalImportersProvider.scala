package io.github.effiban.scala2java.spi.providers

import scala.meta.Importer

/** A provider which specifies additional [[Importer]]-s (individual `import` statements) that should be added
 * to the generated Java source file,  in addition to those already present in the original Scala source file.
 */
trait AdditionalImportersProvider {

  /**
   * @return the additional [[Importer]]-s that should be added to the generated Java source file
   */
  def provide(): List[Importer]
}

object AdditionalImportersProvider {
  /** The default provider which does not provde any additional [[Importer]]-s */
  val Empty: AdditionalImportersProvider = () => Nil
}
