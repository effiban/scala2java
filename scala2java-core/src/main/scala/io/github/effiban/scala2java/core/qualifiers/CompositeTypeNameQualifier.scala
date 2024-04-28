package io.github.effiban.scala2java.core.qualifiers

import io.github.effiban.scala2java.core.importmanipulation.TypeNameImporterMatcher

import scala.meta.{Member, Type}

trait CompositeTypeNameQualifier {

  def qualify(typeName: Type.Name, context: QualificationContext = QualificationContext()): Type
}

private[qualifiers] class CompositeTypeNameQualifierImpl(typeNameImporterMatcher: TypeNameImporterMatcher,
                                                         coreTypeNameQualifier: CoreTypeNameQualifier)
  extends CompositeTypeNameQualifier {

  override def qualify(typeName: Type.Name, context: QualificationContext = QualificationContext()): Type = typeName.parent match {
    case Some(_: Member.Type | _: Type.Param) => typeName
    case _ => qualifyInner(typeName, context)
  }

  private def qualifyInner(typeName: Type.Name, context: QualificationContext): Type = {
    context.importers.map(importer => typeNameImporterMatcher.findMatch(typeName, importer))
      .collectFirst { case Some(importer) => importer }
      .map(importer => Type.Select(importer.ref, typeName))
      .orElse(coreTypeNameQualifier.qualify(typeName))
      .getOrElse(typeName)
  }
}

object CompositeTypeNameQualifier extends CompositeTypeNameQualifierImpl(TypeNameImporterMatcher, CoreTypeNameQualifier)
