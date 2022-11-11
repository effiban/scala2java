package io.github.effiban.scala2java.spi.providers

import scala.meta.Importer

trait DefaultImportersProvider {

  def provide(): List[Importer] = Nil
}

object DefaultImportersProvider {
  val Empty: DefaultImportersProvider = new DefaultImportersProvider {}
}
