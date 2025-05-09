package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.entities.TraversalConstants.JavaPlaceholder
import io.github.effiban.scala2java.core.entities.TypeSelects.JavaThrowable

import scala.meta.{Pat, Term}

trait CatchArgumentTraverser extends ScalaTreeTraverser1[Pat]

private[traversers] class CatchArgumentTraverserImpl(patTraverser: => PatTraverser) extends CatchArgumentTraverser {

  private val DefaultExceptionType = JavaThrowable

  override def traverse(`pat`: Pat): Pat = {
    // TODO - consider adding a Java scope type for the catch argument
    val transformedPat = `pat` match {
      case Pat.Var(name) => Pat.Typed(Pat.Var(name), DefaultExceptionType)
      case Pat.Wildcard() => Pat.Typed(Pat.Var(Term.Name(JavaPlaceholder)), DefaultExceptionType)
      case Pat.Typed(Pat.Wildcard(), tpe) => Pat.Typed(Pat.Var(Term.Name(JavaPlaceholder)), tpe)
      case aPat => aPat
    }
    patTraverser.traverse(transformedPat)
  }
}

