package io.github.effiban.scala2java.core.importmanipulation

import scala.meta.{Importee, Importer, Name, Type, XtensionQuasiquoteType}

trait TypeSelectImporterGenerator {
  def generate(typeSelect: Type.Select): Option[Importer]
}

object TypeSelectImporterGenerator extends TypeSelectImporterGenerator {

  override def generate(typeSelect: Type.Select): Option[Importer] = typeSelect match {
    case t"scala.Array" | t"scala.Enumeration" => None
    case aTypeSelect => Some(
      Importer(
        ref = aTypeSelect.qual,
        importees = List(Importee.Name(Name.Indeterminate(aTypeSelect.name.value)))
      )
    )
  }
}
