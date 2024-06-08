package io.github.effiban.scala2java.core.extractors

import scala.meta.{Name, Type}

object TypeRefNameExtractor {

  def extract(typeRef: Type.Ref): Name = typeRef match {
    case typeName: Type.Name => Name.Indeterminate(typeName.value)
    case typeSelect: Type.Select => Name.Indeterminate(typeSelect.name.value)
    case typeProject: Type.Project => Name.Indeterminate(typeProject.name.value)
    case _ => Name.Anonymous()
  }
}
