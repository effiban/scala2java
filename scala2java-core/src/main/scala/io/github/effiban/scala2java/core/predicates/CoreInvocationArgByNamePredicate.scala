package io.github.effiban.scala2java.core.predicates

import io.github.effiban.scala2java.core.entities.TermNameValues
import io.github.effiban.scala2java.core.entities.TermNameValues.{Apply, JavaOfSupplier, JavaSupplyAsync}
import io.github.effiban.scala2java.spi.entities.InvocationArgCoordinates
import io.github.effiban.scala2java.spi.predicates.InvocationArgByNamePredicate

import scala.meta.Term

object CoreInvocationArgByNamePredicate extends InvocationArgByNamePredicate {

  private final val MethodsWithFirstArgPassedByName: Set[Term] = Set(
    Term.Select(Term.Name(TermNameValues.Try), Term.Name(Apply)),
    Term.Select(Term.Name(TermNameValues.Try), Term.Name(JavaOfSupplier)),
    Term.Select(Term.Name(TermNameValues.Future), Term.Name(Apply)),
    Term.Select(Term.Name(TermNameValues.JavaCompletableFuture), Term.Name(JavaSupplyAsync))
  )


  override def apply(argCoords: InvocationArgCoordinates): Boolean = {
    argCoords match {
      case InvocationArgCoordinates(Term.Apply(method, _), _, 0) if isFirstArgPassedByName(method) => true
      case InvocationArgCoordinates(Term.Apply(Term.ApplyType(method, _), _), _, 0) if isFirstArgPassedByName(method) => true
      case _ => false
    }
  }

  private def isFirstArgPassedByName(method: Term) = MethodsWithFirstArgPassedByName.exists(_.structure == method.structure)
}
