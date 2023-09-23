package io.github.effiban.scala2java.core.qualifiers

import io.github.effiban.scala2java.core.entities.ReflectedEntities.{PredefModule, ScalaPackage}
import io.github.effiban.scala2java.core.entities.{TermNames, TermSelects}

import scala.meta.Type
import scala.reflect.runtime.universe._

trait CoreTypeNameQualifier {
  def qualify(typeName: Type.Name): Option[Type]
}

object CoreTypeNameQualifier extends CoreTypeNameQualifier {

  override def qualify(typeName: Type.Name): Option[Type] = {
      qualifyAsPredefMember(typeName)
        .orElse(qualifyAsScalaPackageMember(typeName))
  }

  private def qualifyAsPredefMember(scalaMetaTypeName: Type.Name) = {
    PredefModule.typeSignature.decl(TypeName(scalaMetaTypeName.value)) match {
      case NoSymbol => None
      case _ => Some(Type.Select(TermSelects.ScalaPredef, scalaMetaTypeName))
    }
  }

  private def qualifyAsScalaPackageMember(scalaMetaTypeName: Type.Name) = {
    ScalaPackage.typeSignature.decl(TypeName(scalaMetaTypeName.value)) match {
      case NoSymbol => None
      case _ => Some(Type.Select(TermNames.Scala, scalaMetaTypeName))
    }
  }
}
