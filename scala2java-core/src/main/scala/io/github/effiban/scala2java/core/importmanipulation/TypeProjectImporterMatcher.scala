package io.github.effiban.scala2java.core.importmanipulation

import scala.meta.{Importee, Importer, Term, Type}

trait TypeProjectImporterMatcher {
  def findMatch(typeProject: Type.Project, importer: Importer): Option[Importer]
}

private[importmanipulation] class TypeProjectImporterMatcherImpl(typeToTermRefConverter: TypeToTermRefConverter)
  extends TypeProjectImporterMatcher {

  override def findMatch(typeProject: Type.Project, importer: Importer): Option[Importer] = {
    typeToTermRefConverter.toTermRefPath(typeProject.qual)
      .filter(qual => qual.structure == importer.ref.structure)
      .flatMap(qual => findByMatchingImportee(qual, typeProject.name, importer))
  }

  private def findByMatchingImportee(qual: Term.Ref, name: Type.Name, importer: Importer) = {
    // TODO use semantic information to match against wildcards
    importer.importees.collectFirst {
      case importee if matchesAnyImportee(name, importer) => importer.copy(importees = List(importee))
    }
  }

  private def matchesAnyImportee(typeName: Type.Name, importer: Importer) = {
    importer.importees.exists {
      case Importee.Name(name) if name.value == typeName.value => true
      case Importee.Wildcard() => true
      case _ => false
    }
  }
}

object TypeProjectImporterMatcher extends TypeProjectImporterMatcherImpl(TypeToTermRefConverter)