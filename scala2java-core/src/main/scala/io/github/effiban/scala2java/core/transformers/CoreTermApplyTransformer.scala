package io.github.effiban.scala2java.core.transformers

import io.github.effiban.scala2java.core.classifiers.TermNameClassifier
import io.github.effiban.scala2java.core.entities.TermNameValues.Apply
import io.github.effiban.scala2java.spi.transformers.TermApplyTransformer

import scala.annotation.tailrec
import scala.meta.Term

private[transformers] class CoreTermApplyTransformer(termNameClassifier: TermNameClassifier) extends TermApplyTransformer {

  // Transform any method invocations which have a Scala-specific style into Java equivalents
  @tailrec
  override final def transform(termApply: Term.Apply): Term.Apply = {
    termApply match {
      case Term.Apply(name : Term.Name, args) => Term.Apply(transformName(name), args)
      case Term.Apply(Term.ApplyType(name: Term.Name, types), args) => Term.Apply(Term.ApplyType(transformName(name), types), args)
      // Invocation of method with more than one param list
      case Term.Apply(Term.Apply(fun, args1), args2) => transform(Term.Apply(fun, args1 ++ args2))
      // Invocation of lambda - must add the implicit apply so it can be further processed by the 'Select' transformer
      case Term.Apply(termFunction: Term.Function, args) => Term.Apply(Term.Select(termFunction, Term.Name(Apply)), args)
      case other => other
    }
  }

  private def transformName(name: Term.Name): Term = name match {
    case nm if termNameClassifier.isPreDefScalaObject(nm) => Term.Select(nm, Term.Name(Apply))
    case _ => name
  }
}

object CoreTermApplyTransformer extends CoreTermApplyTransformer(TermNameClassifier)
