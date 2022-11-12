package io.github.effiban.scala2java.core.transformers

import io.github.effiban.scala2java.core.classifiers.TermNameClassifier

import scala.annotation.tailrec
import scala.meta.Term

trait TermApplyTransformer {
  def transform(termApply: Term.Apply): Term.Apply
}

class TermApplyTransformerImpl(termNameClassifier: TermNameClassifier) extends TermApplyTransformer {

  // Transform any method invocations which have a Scala-specific style into Java equivalents
  @tailrec
  override final def transform(termApply: Term.Apply): Term.Apply = {
    termApply match {
      case Term.Apply(name : Term.Name, args) => Term.Apply(transformName(name), transformArgs(name, args))
      case Term.Apply(Term.ApplyType(name: Term.Name, types), args) => Term.Apply(Term.ApplyType(transformName(name), types), transformArgs(name, args))
      // Invocation of method with more than one param list
      case Term.Apply(Term.Apply(fun, args1), args2) => transform(Term.Apply(fun, args1 ++ args2))
      case other => other
    }
  }

  private def transformName(name: Term.Name): Term = name match {
    case nm if termNameClassifier.isPreDefScalaObject(nm) => Term.Select(nm, Term.Name("apply"))
    case _ => name
  }

  private def transformArgs(name: Term.Name, args: List[Term]): List[Term] = (name, args) match {
    // For objects that are instantiated with a single argument by-name, transform the argument to a supplier lambda for Java
    case (nm, List(arg)) if termNameClassifier.isInstantiatedByName(nm) => List(Term.Function(Nil, arg))
    case _ => args
  }
}

object TermApplyTransformer extends TermApplyTransformerImpl(TermNameClassifier)