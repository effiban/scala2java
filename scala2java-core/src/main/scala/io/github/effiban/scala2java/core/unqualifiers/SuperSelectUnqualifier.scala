package io.github.effiban.scala2java.core.unqualifiers

import io.github.effiban.scala2java.core.binders.FileScopeNonInheritedTermNameBinder
import io.github.effiban.scala2java.core.typeinference.InheritedTermNameOwnersInferrer

import scala.meta.{Name, Term}

trait SuperSelectUnqualifier {

  def unqualify(termSuper: Term.Super, termName: Term.Name): Term.Ref
}

private[unqualifiers] class SuperSelectUnqualifierImpl(inheritedTermNameOwnersInferrer: InheritedTermNameOwnersInferrer,
                                                       fileScopeNonInheritedTermNameBinder: FileScopeNonInheritedTermNameBinder) extends SuperSelectUnqualifier {

  def unqualify(termSuper: Term.Super, termName: Term.Name): Term.Ref = {
    val inheritedTermNameOwners = inheritedTermNameOwnersInferrer.infer(termName)
    val maybeFileScopeTermNameDecl = fileScopeNonInheritedTermNameBinder.bind(termName)
    val maybeSuper = (inheritedTermNameOwners, maybeFileScopeTermNameDecl) match {
      case (inheritedOwners, _) if inheritedOwners.size > 1 => Some(termSuper.copy(superp = Name.Anonymous()))
      case (_, Some(_)) => Some(Term.Super(thisp = Name.Anonymous(), superp = Name.Anonymous()))
      case _ => None
    }
    maybeSuper match {
      case Some(aSuper) => Term.Select(qual = aSuper, name = termName)
      case _ => termName
    }
  }
}

object SuperSelectUnqualifier extends SuperSelectUnqualifierImpl(
  InheritedTermNameOwnersInferrer,
  FileScopeNonInheritedTermNameBinder
)
