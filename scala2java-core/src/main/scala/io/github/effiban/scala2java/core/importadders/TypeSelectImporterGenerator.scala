package io.github.effiban.scala2java.core.importadders

import scala.meta.{Importee, Importer, Name, Type}

trait TypeSelectImporterGenerator {
  def generate(typeSelect: Type.Select): Importer
}

object TypeSelectImporterGenerator extends TypeSelectImporterGenerator {

  override def generate(typeSelect: Type.Select): Importer = {
    Importer(
      ref = typeSelect.qual,
      importees = List(Importee.Name(Name.Indeterminate(typeSelect.name.value)))
    )
  }
}
