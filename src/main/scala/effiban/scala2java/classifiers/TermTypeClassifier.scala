package effiban.scala2java.classifiers

import effiban.scala2java.entities.Decision.{Decision, No, Uncertain, Yes}
import effiban.scala2java.typeinference.TermTypeInferrer

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
