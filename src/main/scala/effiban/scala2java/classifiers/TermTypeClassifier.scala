package effiban.scala2java.classifiers

import effiban.scala2java.entities.Decision.{Decision, No, Uncertain, Yes}
import effiban.scala2java.typeinference.TermTypeInferrer

import scala.meta.{Term, Type}

trait TermTypeClassifier {

  def isReturnable(term: Term): Decision

  def isTupleLike(term: Term): Boolean
}

class TermTypeClassifierImpl(termTypeInferrer: => TermTypeInferrer,
                             termApplyInfixClassifier: TermApplyInfixClassifier) extends TermTypeClassifier {

  override def isReturnable(term: Term): Decision = {
    termTypeInferrer.infer(term) match {
      case Some(Type.Name("Unit")) | Some(Type.AnonymousName()) => No
      case Some(_) => Yes
      case None => Uncertain
    }
  }

  override def isTupleLike(term: Term): Boolean = {
    term match {
      case _ : Term.Tuple => true
      case termApplyInfix: Term.ApplyInfix if termApplyInfixClassifier.isAssociation(termApplyInfix) => true
      case _ => false
    }
  }
}
