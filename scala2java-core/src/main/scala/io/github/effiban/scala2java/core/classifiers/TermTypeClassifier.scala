package io.github.effiban.scala2java.core.classifiers

import io.github.effiban.scala2java.core.entities.Decision.{Decision, No, Uncertain, Yes}
import io.github.effiban.scala2java.core.typeinference.TermTypeInferrer

import scala.meta.{Term, Type}

trait TermTypeClassifier {

  def isReturnable(term: Term): Decision
}

class TermTypeClassifierImpl(termTypeInferrer: => TermTypeInferrer) extends TermTypeClassifier {

  override def isReturnable(term: Term): Decision = {
    termTypeInferrer.infer(term) match {
      case Some(Type.Name("Unit")) | Some(Type.AnonymousName()) => No
      case Some(_) => Yes
      case None => Uncertain
    }
  }

}
