package io.github.effiban.scala2java.core.qualifiers

import io.github.effiban.scala2java.core.binders.FileScopeNonInheritedTermNameBinder
import io.github.effiban.scala2java.core.entities.TreeKeyedMap
import io.github.effiban.scala2java.core.extractors.TreeNameExtractor
import io.github.effiban.scala2java.core.typeinference.{InheritedTermNameOwnersInferrer, InnermostEnclosingTemplateInferrer}

import scala.meta.{Name, Term, XtensionQuasiquoteTerm}

trait InheritedTermNameQualifier {

  def qualify(termName: Term.Name): Option[Term]
}

private[qualifiers] class InheritedTermNameQualifierImpl(innermostEnclosingTemplateInferrer: InnermostEnclosingTemplateInferrer,
                                                         inheritedTermNameOwnersInferrer: InheritedTermNameOwnersInferrer,
                                                         fileScopeNonInheritedTermNameBinder: FileScopeNonInheritedTermNameBinder)
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
    // TODO remove once we handle qualification of file-scope terms properly
    val maybeFileScopeTermNameDecl = fileScopeNonInheritedTermNameBinder.bind(termName)


    (maybeThisp, maybeSuperp, maybeFileScopeTermNameDecl) match {
      case (Some(thisp), Some(superp), None) =>
        Some(Term.Select(Term.Super(thisp, superp), termName))
      case _ => None
    }

  }
}

object InheritedTermNameQualifier extends InheritedTermNameQualifierImpl(
  InnermostEnclosingTemplateInferrer,
  InheritedTermNameOwnersInferrer,
  FileScopeNonInheritedTermNameBinder)
