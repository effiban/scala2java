package io.github.effiban.scala2java.core.predicates

import io.github.effiban.scala2java.core.entities.TermNameValues.Apply
import io.github.effiban.scala2java.core.entities.{ArgumentCoordinates, TermNameValues}

import scala.meta.Term

trait InvocationArgByNamePredicate extends (ArgumentCoordinates => Boolean)

object InvocationArgByNamePredicate extends InvocationArgByNamePredicate {

  private final val MethodsWithFirstArgPassedByName: Set[Term] = Set(
    Term.Select(Term.Name(TermNameValues.Try), Term.Name(Apply)),
    Term.Select(Term.Name(TermNameValues.Future), Term.Name(Apply))
  )


  override def apply(argCoords: ArgumentCoordinates): Boolean = {
    argCoords match {
      case ArgumentCoordinates(Term.Apply(method, _), _, 0) if isFirstArgPassedByName(method) => true
      case ArgumentCoordinates(Term.Apply(Term.ApplyType(method, _), _), _, 0) if isFirstArgPassedByName(method) => true
      case _ => false
    }
  }

  private def isFirstArgPassedByName(method: Term) = MethodsWithFirstArgPassedByName.exists(_.structure == method.structure)
}
