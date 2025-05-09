package io.github.effiban.scala2java.core.qualifiers

import io.github.effiban.scala2java.core.entities.ReflectedEntities.{JavaLangPackage, PredefModule, ScalaPackage}
import io.github.effiban.scala2java.core.reflection.ScalaReflectionUtils.findAsScalaMetaTypeRef

import scala.meta.Type
import scala.reflect.runtime.universe._

trait CoreTypeNameQualifier {
  def qualify(typeName: Type.Name): Option[Type]
}

object CoreTypeNameQualifier extends CoreTypeNameQualifier {

  override def qualify(typeName: Type.Name): Option[Type] = {
    LazyList(
      qualifyAsPredefMember _,
      qualifyAsScalaPackageMember _,
      qualifyAsJavaLangMember _
    ).map(_.apply(typeName))
      .collectFirst { case Some(tpe) => tpe }
  }

  private def qualifyAsPredefMember(scalaMetaTypeName: Type.Name) = {
    qualifyAsMemberOf(PredefModule, scalaMetaTypeName)
  }

  private def qualifyAsScalaPackageMember(scalaMetaTypeName: Type.Name) = {
    qualifyAsMemberOf(ScalaPackage, scalaMetaTypeName)
  }

  private def qualifyAsJavaLangMember(scalaMetaTypeName: Type.Name) = {
    qualifyAsMemberOf(JavaLangPackage, scalaMetaTypeName)
  }

  private def qualifyAsMemberOf(module: ModuleSymbol, typeName: Type.Name) = {
    findAsScalaMetaTypeRef(module, typeName)
  }
}
