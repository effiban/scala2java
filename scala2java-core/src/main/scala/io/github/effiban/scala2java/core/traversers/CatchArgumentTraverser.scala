package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.entities.TraversalConstants.JavaPlaceholder

import scala.meta.{Pat, Term, XtensionQuasiquoteType}

trait CatchArgumentTraverser extends ScalaTreeTraverser1[Pat]

object CatchArgumentTraverser extends CatchArgumentTraverser {

  private val DefaultExceptionType = t"Throwable"

  override def traverse(`pat`: Pat): Pat = {
    // TODO - consider adding a Java scope type for the catch argument
    `pat` match {
      case Pat.Var(name) => Pat.Typed(Pat.Var(name), DefaultExceptionType)
      case Pat.Wildcard() => Pat.Typed(Pat.Var(Term.Name(JavaPlaceholder)), DefaultExceptionType)
      case Pat.Typed(Pat.Wildcard(), tpe) => Pat.Typed(Pat.Var(Term.Name(JavaPlaceholder)), tpe)
      case aPat => aPat
    }
  }
}

