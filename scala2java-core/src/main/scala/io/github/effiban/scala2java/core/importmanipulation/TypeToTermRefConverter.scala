package io.github.effiban.scala2java.core.importmanipulation

import scala.meta.{Term, Type}

private[importmanipulation] trait TypeToTermRefConverter {
  def toTermRefPath(tpe: Type): Option[Term.Ref]
}

private[importmanipulation] object TypeToTermRefConverter extends TypeToTermRefConverter {

  override def toTermRefPath(tpe: Type): Option[Term.Ref] = {
    tpe match {
      case typeName: Type.Name => Some(asTermName(typeName))
      case typeSelect: Type.Select => Some(asTermSelect(typeSelect))
      case typeProject: Type.Project => asTermSelect(typeProject)
      case _ => None
    }
  }

  private def asTermName(typeName: Type.Name) = Term.Name(typeName.value)

  private def asTermSelect(typeSelect: Type.Select) = Term.Select(typeSelect.qual, asTermName(typeSelect.name))

  private def asTermSelect(typeProject: Type.Project): Option[Term.Select] = {
    toTermRefPath(typeProject.qual).map(qual => Term.Select(qual, asTermName(typeProject.name)))
  }
}

