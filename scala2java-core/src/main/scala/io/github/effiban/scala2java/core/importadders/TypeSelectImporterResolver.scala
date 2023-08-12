package io.github.effiban.scala2java.core.importadders

import scala.meta.{Importee, Importer, Name, Type}

trait TypeSelectImporterResolver {
  def resolve(typeSelect: Type.Select): Importer
}

object TypeSelectImporterResolver extends TypeSelectImporterResolver {

  override def resolve(typeSelect: Type.Select): Importer = {
    Importer(
      ref = typeSelect.qual,
      importees = List(Importee.Name(Name.Indeterminate(typeSelect.name.value)))
    )
  }
}
