package io.github.effiban.scala2java.core.classifiers

import scala.meta.Term.{ApplyType, Ascribe, Eta, ForYield, New, NewAnonymous}
import scala.meta.{Lit, Term}

trait TermTreeClassifier {

  def isReturnable(term: Term): Boolean
}

object TermTreeClassifier extends TermTreeClassifier {

  override def isReturnable(term: Term): Boolean = {
    term match {
      case _: Term.Apply |
           _: Term.ApplyInfix |
           _: ApplyType |
           _: Ascribe |
           _: ForYield |
           _: Term.Function |
           _: Term.PartialFunction |
           _: Term.AnonymousFunction |
           _: Term.Interpolate |
           _: Lit |
           _: Term.Name |
           _: New |
           _: NewAnonymous |
           _: Term.Repeated |
           _: Term.Select |
           _: Term.Match |
           _: Term.Tuple |
           _: Eta => true
      case _ => false
    }
  }
}
