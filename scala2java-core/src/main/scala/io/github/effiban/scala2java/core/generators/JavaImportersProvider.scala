package io.github.effiban.scala2java.core.generators

import scala.meta.Importer

trait JavaImportersProvider {
  def provide(): List[Importer]
}
