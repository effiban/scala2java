package io.github.effiban.scala2java.core.classifiers

import io.github.effiban.scala2java.core.contexts.ArgumentContext
import io.github.effiban.scala2java.core.entities.TermNameValues
import io.github.effiban.scala2java.core.entities.TermNameValues.Apply

import scala.meta.Term

trait InvocationArgClassifier {

  def isPassedByName(argContext: ArgumentContext): Boolean
}

object InvocationArgClassifier extends InvocationArgClassifier {

  private final val MethodsWithFirstArgPassedByName: Set[Term] = Set(
    Term.Select(Term.Name(TermNameValues.Try), Term.Name(Apply)),
    Term.Select(Term.Name(TermNameValues.Future), Term.Name(Apply))
  )


  override def isPassedByName(argContext: ArgumentContext): Boolean = {
    argContext match {
      case ArgumentContext(Some(Term.Apply(method, _)), _, 0, _) if isFirstArgPassedByName(method) => true
      case ArgumentContext(Some(Term.Apply(Term.ApplyType(method, _), _)), _, 0, _) if isFirstArgPassedByName(method) => true
      case _ => false
    }
  }

  private def isFirstArgPassedByName(method: Term) = MethodsWithFirstArgPassedByName.exists(_.structure == method.structure)
}
