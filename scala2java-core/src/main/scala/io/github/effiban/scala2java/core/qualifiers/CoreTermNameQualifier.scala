package io.github.effiban.scala2java.core.qualifiers

import io.github.effiban.scala2java.core.entities.TermNames.Scala
import io.github.effiban.scala2java.core.entities.TermSelects.{JavaLang, ScalaPredef}
import io.github.effiban.scala2java.core.reflection.ScalaReflectionLookup.findModuleTermMemberOf

import scala.meta.Term

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
    qualifyAsMemberOf(ScalaPredef, scalaMetaTermName)
  }

  private def qualifyAsScalaPackageMember(scalaMetaTermName: Term.Name) = {
    qualifyAsMemberOf(Scala, scalaMetaTermName)
  }

  private def qualifyAsJavaLangMember(scalaMetaTermName: Term.Name) = {
    qualifyAsMemberOf(JavaLang, scalaMetaTermName)
  }

  private def qualifyAsMemberOf(module: Term.Ref,
                                termName: Term.Name): Option[Term] = {
    findModuleTermMemberOf(module, termName)
  }
}