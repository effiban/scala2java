package io.github.effiban.scala2java.core.qualifiers

import io.github.effiban.scala2java.core.entities.TermNames.Scala
import io.github.effiban.scala2java.core.entities.TermSelects.{JavaLang, ScalaPredef}
import io.github.effiban.scala2java.core.reflection.ScalaReflectionLookup

import scala.meta.{Term, Type}

trait CoreTypeNameQualifier {
  def qualify(typeName: Type.Name): Option[Type]
}

private[qualifiers] class CoreTypeNameQualifierImpl(scalaReflectionLookup: ScalaReflectionLookup) extends CoreTypeNameQualifier {
  override def qualify(typeName: Type.Name): Option[Type] = {
    LazyList(
      qualifyAsPredefMember _,
      qualifyAsScalaPackageMember _,
      qualifyAsJavaLangMember _
    ).map(_.apply(typeName))
      .collectFirst { case Some(tpe) => tpe }
  }

  private def qualifyAsPredefMember(scalaMetaTypeName: Type.Name) = {
    qualifyAsMemberOf(ScalaPredef, scalaMetaTypeName)
  }

  private def qualifyAsScalaPackageMember(scalaMetaTypeName: Type.Name) = {
    qualifyAsMemberOf(Scala, scalaMetaTypeName)
  }

  private def qualifyAsJavaLangMember(scalaMetaTypeName: Type.Name) = {
    qualifyAsMemberOf(JavaLang, scalaMetaTypeName)
  }

  private def qualifyAsMemberOf(module: Term.Ref, typeName: Type.Name) = {
    scalaReflectionLookup.findModuleTypeMemberOf(module, typeName)
  }
}

object CoreTypeNameQualifier extends CoreTypeNameQualifierImpl(ScalaReflectionLookup)
