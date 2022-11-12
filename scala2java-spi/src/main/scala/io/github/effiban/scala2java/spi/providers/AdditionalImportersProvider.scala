package io.github.effiban.scala2java.spi.providers

import scala.meta.Importer

trait AdditionalImportersProvider {

  def provide(): List[Importer] = Nil
}

object AdditionalImportersProvider {
  val Empty: AdditionalImportersProvider = new AdditionalImportersProvider {}
}
