package io.github.effiban.scala2java.core.qualifiers

import io.github.effiban.scala2java.core.entities.ReflectedEntities.{PredefModule, ScalaPackage}
import io.github.effiban.scala2java.core.entities.{TermNames, TermSelects}

import scala.meta.Term
import scala.reflect.runtime.universe._

trait CoreTermNameQualifier {
  def qualify(termName: Term.Name): Option[Term]
}

object CoreTermNameQualifier extends CoreTermNameQualifier {

  override def qualify(termName: Term.Name): Option[Term] = {
      qualifyAsPredefMember(termName)
        .orElse(qualifyAsScalaPackageMember(termName))
  }

  private def qualifyAsPredefMember(scalaMetaTermName: Term.Name) = {
    PredefModule.info.member(TermName(scalaMetaTermName.value)) match {
      case NoSymbol => None
      case _ => Some(Term.Select(TermSelects.ScalaPredef, scalaMetaTermName))
    }
  }

  private def qualifyAsScalaPackageMember(scalaMetaTermName: Term.Name) = {
    ScalaPackage.info.member(TermName(scalaMetaTermName.value)) match {
      case NoSymbol => None
      case _ => Some(Term.Select(TermNames.Scala, scalaMetaTermName))
    }
  }
}
