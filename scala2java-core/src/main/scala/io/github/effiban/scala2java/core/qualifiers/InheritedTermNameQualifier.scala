package io.github.effiban.scala2java.core.qualifiers

import io.github.effiban.scala2java.core.entities.TreeKeyedMap
import io.github.effiban.scala2java.core.extractors.TreeNameExtractor
import io.github.effiban.scala2java.core.importmanipulation.TermNameImporterMatcher
import io.github.effiban.scala2java.core.typeinference.{InheritedTermNameOwnersInferrer, InnermostEnclosingTemplateInferrer}

import scala.meta.{Importer, Member, Name, Term, XtensionQuasiquoteTerm}

trait InheritedTermNameQualifier {

  def qualify(termName: Term.Name): Option[Term]
}

private[qualifiers] class InheritedTermNameQualifierImpl(innermostEnclosingTemplateInferrer: InnermostEnclosingTemplateInferrer,
                                                         inheritedTermNameOwnersInferrer: InheritedTermNameOwnersInferrer)
  extends InheritedTermNameQualifier {

  override def qualify(termName: Term.Name): Option[Term] = {
    val maybeEnclosingTemplate = innermostEnclosingTemplateInferrer.infer(termName)

    val maybeThisp = maybeEnclosingTemplate
      .flatMap(_.parent)
      .map(TreeNameExtractor.extract)

    val maybeSuperp = maybeEnclosingTemplate.flatMap(enclosingTemplate => {
      val inheritedTermNameOwners = inheritedTermNameOwnersInferrer.infer(termName)
      TreeKeyedMap.get(inheritedTermNameOwners, enclosingTemplate).getOrElse(Nil)
        .headOption
        .map(TreeNameExtractor.extract)
    })

    (maybeThisp, maybeSuperp) match {
      case (Some(thisp), Some(superp)) if !isEnumValue(superp, termName) =>
        Some(Term.Select(Term.Super(thisp, superp), termName))
      case _ => None
    }

  }

  // Skip qualification of Enumeration Value member since it has special logic
  // TODO - remove this once the same-file scopes are handled at an earlier stage
  private def isEnumValue(enclosingTypeName: Name, termName: Term.Name) = {
    enclosingTypeName.value == "Enumeration" && termName.structure == q"Value".structure
  }
}

object InheritedTermNameQualifier extends InheritedTermNameQualifierImpl(
  InnermostEnclosingTemplateInferrer,
  InheritedTermNameOwnersInferrer)
