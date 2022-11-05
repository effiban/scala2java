package io.github.effiban.scala2java.spi.providers

import scala.meta.Importer

trait JavaImportersProvider {

  def provide(): List[Importer] = Nil
}

object JavaImportersProvider {
  val Empty: JavaImportersProvider = new JavaImportersProvider {}
}
