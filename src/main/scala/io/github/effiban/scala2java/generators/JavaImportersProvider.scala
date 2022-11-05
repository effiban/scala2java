package io.github.effiban.scala2java.generators

import scala.meta.Importer

trait JavaImportersProvider {
  def provide(): List[Importer]
}
