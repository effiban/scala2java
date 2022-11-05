package io.github.effiban.scala2java.core.resolvers

import io.github.effiban.scala2java.core.classifiers.TermTypeClassifier
import io.github.effiban.scala2java.core.entities.Decision.{Decision, No, Uncertain, Yes}

import scala.meta.Term

trait ShouldReturnValueResolver {

  def resolve(term: Term, parentShouldReturnValue: Decision): Decision
}

class ShouldReturnValueResolverImpl(termTypeClassifier: => TermTypeClassifier) extends ShouldReturnValueResolver {

  override def resolve(term: Term, parentShouldReturnValue: Decision): Decision = {
    parentShouldReturnValue match {
      case Yes | Uncertain if termTypeClassifier.isReturnable(term) == No => No
      case other => other
    }
  }
}