package io.github.effiban.scala2java.core.unqualifiers

import io.github.effiban.scala2java.core.importmanipulation.TypeProjectImporterMatcher
import io.github.effiban.scala2java.core.qualifiers.QualificationContext

import scala.meta.Type

trait TypeProjectUnqualifier {

  def unqualify(typeProject: Type.Project, context: QualificationContext = QualificationContext()): Type.Ref
}

private[unqualifiers] class TypeProjectUnqualifierImpl(typeProjectImporterMatcher: TypeProjectImporterMatcher)
  extends TypeProjectUnqualifier {

  override def unqualify(typeProject: Type.Project, context: QualificationContext = QualificationContext()): Type.Ref = {
    context.importers.map(importer => typeProjectImporterMatcher.findMatch(typeProject, importer))
      .collectFirst {
        case Some(_) => typeProject.name
      }
      .getOrElse(typeProject)
  }
}

object TypeProjectUnqualifier extends TypeProjectUnqualifierImpl(TypeProjectImporterMatcher)
