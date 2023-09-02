package io.github.effiban.scala2java.core.qualifiers

import scala.meta.{Term, Type}
import scala.reflect.runtime.universe._

trait CoreTypeNameQualifier {
  def qualify(typeName: Type.Name): Option[Type]
}

object CoreTypeNameQualifier extends CoreTypeNameQualifier {

  private val mirror = runtimeMirror(getClass.getClassLoader)

  override def qualify(typeName: Type.Name): Option[Type] = {
      qualifyAsPredefMember(typeName)
        .orElse(qualifyAsScalaPackageMember(typeName))
  }

  private def qualifyAsPredefMember(scalaMetaTypeName: Type.Name) = {
    mirror.staticModule("scala.Predef").typeSignature.decl(TypeName(scalaMetaTypeName.value)) match {
      case NoSymbol => None
      case _ => Some(Type.Select(Term.Select(Term.Name("scala"), Term.Name("Predef")), scalaMetaTypeName))
    }
  }

  private def qualifyAsScalaPackageMember(scalaMetaTypeName: Type.Name) = {
    mirror.staticPackage("scala").typeSignature.decl(TypeName(scalaMetaTypeName.value)) match {
      case NoSymbol => None
      case _ => Some(Type.Select(Term.Name("scala"), scalaMetaTypeName))
    }
  }
}
