package io.github.effiban.scala2java.core.unqualifiers

import scala.meta.Type

trait TypeSelectUnqualifier {

  def unqualify(typeSelect: Type.Select): Type
}

object TypeSelectUnqualifier extends TypeSelectUnqualifier {
  override def unqualify(typeSelect: Type.Select): Type = {
    // TODO support partial unqualification according to input Importer(s)
    typeSelect.name
  }
}
