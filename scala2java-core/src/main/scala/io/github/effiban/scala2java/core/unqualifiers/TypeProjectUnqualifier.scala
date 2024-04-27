package io.github.effiban.scala2java.core.unqualifiers

import io.github.effiban.scala2java.core.importmanipulation.TypeProjectImporterMatcher

import scala.meta.{Importer, Type}

trait TypeProjectUnqualifier {

  def unqualify(typeProject: Type.Project, importers: List[Importer] = Nil): Type.Ref
}

private[unqualifiers] class TypeProjectUnqualifierImpl(typeProjectImporterMatcher: TypeProjectImporterMatcher)
  extends TypeProjectUnqualifier {

  override def unqualify(typeProject: Type.Project, importers: List[Importer] = Nil): Type.Ref = {
    importers.map(importer => typeProjectImporterMatcher.findMatch(typeProject, importer))
      .collectFirst {
        case Some(_) => typeProject.name
      }
      .getOrElse(typeProject)
  }
}

object TypeProjectUnqualifier extends TypeProjectUnqualifierImpl(TypeProjectImporterMatcher)
