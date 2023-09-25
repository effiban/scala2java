package io.github.effiban.scala2java.core.qualifiers

import io.github.effiban.scala2java.core.importmanipulation.TypeNameImporterMatcher

import scala.meta.{Importer, Member, Type}

trait CompositeTypeNameQualifier {

  def qualify(typeName: Type.Name, importers: List[Importer] = Nil): Type
}

private[qualifiers] class CompositeTypeNameQualifierImpl(typeNameImporterMatcher: TypeNameImporterMatcher,
                                                         coreTypeNameQualifier: CoreTypeNameQualifier)
  extends CompositeTypeNameQualifier {

  override def qualify(typeName: Type.Name, importers: List[Importer] = Nil): Type = typeName.parent match {
    case Some(_: Member.Type | _: Type.Param) => typeName
    case _ => qualifyInner(typeName, importers)
  }

  private def qualifyInner(typeName: Type.Name, importers: List[Importer] = Nil): Type = {
    importers.map(importer => typeNameImporterMatcher.findMatch(typeName, importer))
      .collectFirst { case Some(importer) => importer }
      .map(importer => Type.Select(importer.ref, typeName))
      .orElse(coreTypeNameQualifier.qualify(typeName))
      .getOrElse(typeName)
  }
}

object CompositeTypeNameQualifier extends CompositeTypeNameQualifierImpl(TypeNameImporterMatcher, CoreTypeNameQualifier)
