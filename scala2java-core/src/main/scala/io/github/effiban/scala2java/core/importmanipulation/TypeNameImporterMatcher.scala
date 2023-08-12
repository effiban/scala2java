package io.github.effiban.scala2java.core.importmanipulation

import scala.meta.{Importee, Importer, Type}

trait TypeNameImporterMatcher {
  def findMatch(typeName: Type.Name, importer: Importer): Option[Importer]
}

object TypeNameImporterMatcher extends TypeNameImporterMatcher {

  override def findMatch(typeName: Type.Name, importer: Importer): Option[Importer] = {
    // TODO use semantic information to match against wildcards
    importer.importees.collectFirst {
      case importee@Importee.Name(name) if name.value == typeName.value => importer.copy(importees = List(importee))
    }
  }
}
