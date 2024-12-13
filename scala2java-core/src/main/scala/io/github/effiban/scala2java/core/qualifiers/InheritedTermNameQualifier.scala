package io.github.effiban.scala2java.core.qualifiers

import io.github.effiban.scala2java.core.binders.FileScopeNonInheritedTermNameBinder
import io.github.effiban.scala2java.core.extractors.TreeNameExtractor
import io.github.effiban.scala2java.core.typeinference.InheritedTermNameOwnersInferrer

import scala.meta.Term

trait InheritedTermNameQualifier {

  def qualify(termName: Term.Name, context: QualificationContext): Option[Term]
}

private[qualifiers] class InheritedTermNameQualifierImpl(inheritedTermNameOwnersInferrer: InheritedTermNameOwnersInferrer,
                                                         fileScopeNonInheritedTermNameBinder: FileScopeNonInheritedTermNameBinder)
  extends InheritedTermNameQualifier {

  override def qualify(termName: Term.Name, context: QualificationContext): Option[Term] = {
    val inheritedTermNameOwners = inheritedTermNameOwnersInferrer.inferAll(termName, context)

    val (maybeEnclosingTemplate, maybeInnermostOwner) = inheritedTermNameOwners
      .headOption
      .map { case (templ, ancestors) => (templ, ancestors.head) }
      .unzip

    val maybeThisp = maybeEnclosingTemplate
      .flatMap(_.parent)
      .map(TreeNameExtractor.extractIndeterminate)

    val maybeSuperp = maybeInnermostOwner.map(TreeNameExtractor.extractIndeterminate)

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
  InheritedTermNameOwnersInferrer,
  FileScopeNonInheritedTermNameBinder)
