package io.github.effiban.scala2java.core.transformers

import io.github.effiban.scala2java.core.classifiers.TermNameClassifier
import io.github.effiban.scala2java.core.entities.TermNameValues.Apply
import io.github.effiban.scala2java.spi.transformers.TermApplyTransformer

import scala.annotation.tailrec
import scala.meta.Term

trait InternalTermApplyTransformer {
  def transform(termApply: Term.Apply): Term.Apply
}

private[transformers] class InternalTermApplyTransformerImpl(termApplyTransformer: TermApplyTransformer,
                                                             termNameClassifier: TermNameClassifier) extends InternalTermApplyTransformer {

  @tailrec
  override final def transform(termApply: Term.Apply): Term.Apply = {
    termApply match {
      case Term.Apply(name : Term.Name, args) if termNameClassifier.hasApplyMethod(name) => transform(Term.Apply(toQualifiedApply(name), args))
      case Term.Apply(Term.ApplyType(name: Term.Name, types), args) if termNameClassifier.hasApplyMethod(name) =>
        transform(Term.Apply(Term.ApplyType(toQualifiedApply(name), types), args))
      // Invocation of method with more than one param list
      case Term.Apply(Term.Apply(fun, args1), args2) => transform(Term.Apply(fun, args1 ++ args2))
      // Invocation of lambda - must add the implicit apply so it can be further processed by the 'Select' transformer
      case Term.Apply(termFunction: Term.Function, args) => transform(Term.Apply(Term.Select(termFunction, Term.Name(Apply)), args))

      case other => termApplyTransformer.transform(other)
    }
  }

  private def toQualifiedApply(name: Term.Name): Term = name match {
    case nm if termNameClassifier.hasApplyMethod(nm) => Term.Select(nm, Term.Name(Apply))
    case _ => name
  }
}
