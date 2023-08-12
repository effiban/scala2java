package io.github.effiban.scala2java.core.unqualifiers

import io.github.effiban.scala2java.core.importmanipulation.TypeSelectImporterMatcher

import scala.meta.{Importer, Type}

trait TypeSelectUnqualifier {

  def unqualify(typeSelect: Type.Select, importers: List[Importer] = Nil): Type
}

private[unqualifiers] class TypeSelectUnqualifierImpl(typeSelectImporterMatcher: TypeSelectImporterMatcher) extends TypeSelectUnqualifier {

  override def unqualify(typeSelect: Type.Select, importers: List[Importer] = Nil): Type = {
    // TODO support partial unqualification once support is added to the matcher
    if (importers.exists(importer => typeSelectImporterMatcher.matches(typeSelect, importer))) typeSelect.name else typeSelect
  }
}

object TypeSelectUnqualifier extends TypeSelectUnqualifierImpl(TypeSelectImporterMatcher)
