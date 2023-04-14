package io.github.effiban.scala2java.core.transformers

import io.github.effiban.scala2java.core.entities.TermNameValues.Apply
import io.github.effiban.scala2java.spi.contexts.TermApplyTransformationContext
import io.github.effiban.scala2java.spi.predicates.TermNameHasApplyMethod
import io.github.effiban.scala2java.spi.transformers.TermApplyTransformer

import scala.annotation.tailrec
import scala.meta.Term

trait InternalTermApplyTransformer {
  def transform(termApply: Term.Apply, context: TermApplyTransformationContext = TermApplyTransformationContext()): Term.Apply
}

private[transformers] class InternalTermApplyTransformerImpl(termApplyTransformer: TermApplyTransformer,
                                                             termNameHasApplyMethod: TermNameHasApplyMethod) extends InternalTermApplyTransformer {

  @tailrec
  override final def transform(termApply: Term.Apply, context: TermApplyTransformationContext = TermApplyTransformationContext()): Term.Apply = {
    termApply match {
      case Term.Apply(name : Term.Name, args) if termNameHasApplyMethod(name) => transform(Term.Apply(toQualifiedApply(name), args), context)
      case Term.Apply(Term.ApplyType(name: Term.Name, types), args) if termNameHasApplyMethod(name) =>
        transform(Term.Apply(Term.ApplyType(toQualifiedApply(name), types), args), context)
      // Invocation of method with more than one param list
      case Term.Apply(Term.Apply(fun, args1), args2) => transform(Term.Apply(fun, args1 ++ args2), context)
      // Invocation of lambda - we must add the implicit apply just like case classes and objects
      case Term.Apply(termFunction: Term.Function, args) => transform(Term.Apply(Term.Select(termFunction, Term.Name(Apply)), args), context)

      case other => termApplyTransformer.transform(other, context)
    }
  }

  private def toQualifiedApply(name: Term.Name): Term = Term.Select(name, Term.Name(Apply))
}
