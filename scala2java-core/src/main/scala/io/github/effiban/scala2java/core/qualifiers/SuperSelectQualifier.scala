package io.github.effiban.scala2java.core.qualifiers

import io.github.effiban.scala2java.core.entities.TreeKeyedMap
import io.github.effiban.scala2java.core.extractors.TreeNameExtractor
import io.github.effiban.scala2java.core.typeinference.{InheritedTermNameOwnersInferrer, InnermostEnclosingTemplateInferrer}

import scala.meta.{Name, Term}

trait SuperSelectQualifier {

  def qualify(termSuper: Term.Super,
              termName: Term.Name,
              context: QualificationContext = QualificationContext()): Term.Select
}

private[qualifiers] class SuperSelectQualifierImpl(innermostEnclosingTemplateInferrer: InnermostEnclosingTemplateInferrer,
                                                   inheritedTermNameOwnersInferrer: InheritedTermNameOwnersInferrer) extends SuperSelectQualifier {

  override def qualify(termSuper: Term.Super,
                       termName: Term.Name,
                       context: QualificationContext = QualificationContext()): Term.Select = {
    val maybeEnclosingMemberName = termSuper.thisp match {
      case Name.Anonymous() => None
      case thisp => Some(thisp.value)
    }
    val maybeEnclosingTemplate = innermostEnclosingTemplateInferrer.infer(termSuper, maybeEnclosingMemberName)

    val qualifiedThisp = maybeEnclosingMemberName match {
      case Some(name) => Name.Indeterminate(name)
      case None => maybeEnclosingTemplate
        .flatMap(_.parent)
        .map(TreeNameExtractor.extract)
        .getOrElse(Name.Anonymous())
    }

    val qualifiedSuperp = (termSuper.superp, maybeEnclosingTemplate) match {
      case (Name.Anonymous(), Some(enclosingTemplate)) =>
        val inheritedTermNameOwners = inheritedTermNameOwnersInferrer.infer(termName)
        TreeKeyedMap.get(inheritedTermNameOwners, enclosingTemplate).getOrElse(Nil)
          .headOption
          .map(TreeNameExtractor.extract)
          .getOrElse(Name.Anonymous())
      case (Name.Anonymous(), _) => Name.Anonymous()
      case (superp, _) => superp
    }
    Term.Select(Term.Super(qualifiedThisp, qualifiedSuperp), termName)
  }
}

object SuperSelectQualifier extends SuperSelectQualifierImpl(
  InnermostEnclosingTemplateInferrer,
  InheritedTermNameOwnersInferrer
)
