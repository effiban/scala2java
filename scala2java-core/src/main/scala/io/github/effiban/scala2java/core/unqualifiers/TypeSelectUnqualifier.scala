package io.github.effiban.scala2java.core.unqualifiers

import io.github.effiban.scala2java.core.importmanipulation.TypeSelectImporterMatcher

import scala.meta.{Importer, Type}

trait TypeSelectUnqualifier {

  def unqualify(typeSelect: Type.Select, importers: List[Importer] = Nil): Type.Ref
}

private[unqualifiers] class TypeSelectUnqualifierImpl(typeSelectImporterMatcher: TypeSelectImporterMatcher) extends TypeSelectUnqualifier {

  override def unqualify(typeSelect: Type.Select, importers: List[Importer] = Nil): Type.Ref = {

    typeSelect match {
      case aTypeSelect =>
        // TODO support partial unqualification once support is added to the matcher
        importers.map(importer => typeSelectImporterMatcher.findMatch(aTypeSelect, importer))
          .collectFirst {
            case Some(_) => aTypeSelect.name
          }
          .getOrElse(aTypeSelect)
    }
  }
}

object TypeSelectUnqualifier extends TypeSelectUnqualifierImpl(TypeSelectImporterMatcher)
