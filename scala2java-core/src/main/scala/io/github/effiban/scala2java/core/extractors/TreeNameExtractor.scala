package io.github.effiban.scala2java.core.extractors

import scala.meta.{Member, Name, Tree, Type}

object TreeNameExtractor {

  def extract(tree: Tree): Name = tree match {
    case typeName: Type.Name => Name.Indeterminate(typeName.value)
    case typeSelect: Type.Select => Name.Indeterminate(typeSelect.name.value)
    case typeProject: Type.Project => Name.Indeterminate(typeProject.name.value)
    case member: Member => member.name
    case _ => Name.Anonymous()
  }
}
