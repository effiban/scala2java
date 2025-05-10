package io.github.effiban.scala2java.core.qualifiers

import io.github.effiban.scala2java.core.entities.ReflectedEntities.{JavaLangPackage, PredefModule, ScalaPackage}
import io.github.effiban.scala2java.core.entities.{TermNames, TermSelects}
import io.github.effiban.scala2java.core.reflection.ScalaReflectionUtils.{findAndDealiasAsScalaMetaTermRef, isTermMemberOf}

import scala.meta.Term
import scala.reflect.runtime.universe._

trait CoreTermNameQualifier {
  def qualify(termName: Term.Name): Option[Term]
}

object CoreTermNameQualifier extends CoreTermNameQualifier {

  override def qualify(termName: Term.Name): Option[Term] = {
    LazyList(
      qualifyAsPredefMember _,
      qualifyAsScalaPackageMember _,
      qualifyAsJavaLangMember _
    ).map(_.apply(termName))
      .collectFirst { case Some(term) => term }
  }

  private def qualifyAsPredefMember(scalaMetaTermName: Term.Name) = {
    qualifyAsMemberOf(PredefModule, scalaMetaTermName)
  }

  private def qualifyAsScalaPackageMember(scalaMetaTermName: Term.Name) = {
    qualifyAsMemberOf(ScalaPackage, scalaMetaTermName)
  }

  private def qualifyAsJavaLangMember(scalaMetaTermName: Term.Name): Option[Term] = {
    qualifyAsMemberOf(JavaLangPackage, scalaMetaTermName)
  }

  private def qualifyAsMemberOf(module: ModuleSymbol,
                                termName: Term.Name): Option[Term] = {
    findAndDealiasAsScalaMetaTermRef(module, termName)
  }
}