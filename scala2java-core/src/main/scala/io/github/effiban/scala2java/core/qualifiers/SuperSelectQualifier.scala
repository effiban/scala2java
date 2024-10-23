package io.github.effiban.scala2java.core.qualifiers

import io.github.effiban.scala2java.core.entities.TreeKeyedMap
import io.github.effiban.scala2java.core.extractors.TreeNameExtractor
import io.github.effiban.scala2java.core.typeinference.{InheritedTermNameOwnersInferrer, InnermostEnclosingTemplateInferrer}

import scala.meta.{Name, Term}

trait SuperSelectQualifier {

  def qualify(termSuper: Term.Super,
              termName: Term.Name,
              context: QualificationContext): Term.Select
}

private[qualifiers] class SuperSelectQualifierImpl(innermostEnclosingTemplateInferrer: InnermostEnclosingTemplateInferrer,
                                                   inheritedTermNameOwnersInferrer: InheritedTermNameOwnersInferrer) extends SuperSelectQualifier {

  override def qualify(termSuper: Term.Super,
                       termName: Term.Name,
                       context: QualificationContext): Term.Select = {
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

    val qualifiedSuperp = termSuper.superp match {
      case Name.Anonymous() => inheritedTermNameOwnersInferrer.infer(termName, context)
        .values
        .flatten
        .headOption
        .map(TreeNameExtractor.extract)
        .getOrElse(Name.Anonymous())
      case superp => superp
    }
    Term.Select(Term.Super(qualifiedThisp, qualifiedSuperp), termName)
  }
}

object SuperSelectQualifier extends SuperSelectQualifierImpl(
  InnermostEnclosingTemplateInferrer,
  InheritedTermNameOwnersInferrer
)
