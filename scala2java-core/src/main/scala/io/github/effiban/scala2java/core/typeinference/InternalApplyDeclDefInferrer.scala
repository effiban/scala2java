package io.github.effiban.scala2java.core.typeinference

import io.github.effiban.scala2java.core.entities.TermNameValues.Apply
import io.github.effiban.scala2java.core.predicates.TermNameHasApplyMethod
import io.github.effiban.scala2java.spi.contexts.TermApplyInferenceContext
import io.github.effiban.scala2java.spi.entities.PartialDeclDef
import io.github.effiban.scala2java.spi.typeinferrers.ApplyDeclDefInferrer

import scala.annotation.tailrec
import scala.meta.Term

trait InternalApplyDeclDefInferrer {
  def infer(termApply: Term.Apply, context: TermApplyInferenceContext): PartialDeclDef
}

private[typeinference] class InternalApplyDeclDefInferrerImpl(applyDeclDefInferrer: => ApplyDeclDefInferrer,
                                                              termNameHasApplyMethod: TermNameHasApplyMethod)
  extends InternalApplyDeclDefInferrer {

  @tailrec
  final def infer(termApply: Term.Apply, context: TermApplyInferenceContext): PartialDeclDef = {
    (termApply.fun, termApply.args) match {

      case (termName: Term.Name, args) if termNameHasApplyMethod(termName) =>
        val adjustedTermApply = Term.Apply(Term.Select(termName, Term.Name(Apply)), args)
        infer(adjustedTermApply, context)

      case (Term.ApplyType(termName: Term.Name, targs), args) if termNameHasApplyMethod(termName) =>
        val adjustedTermApply = Term.Apply(
          Term.ApplyType(Term.Select(termName, Term.Name(Apply)), targs),
          args
        )
        infer(adjustedTermApply, context)

      case _ => applyDeclDefInferrer.infer(termApply, context)
    }
  }
}