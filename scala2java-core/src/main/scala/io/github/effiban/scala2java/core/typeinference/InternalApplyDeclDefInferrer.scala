package io.github.effiban.scala2java.core.typeinference

import io.github.effiban.scala2java.core.entities.TypeSelects.ScalaAny
import io.github.effiban.scala2java.core.predicates.TermSelectHasApplyMethod
import io.github.effiban.scala2java.core.reflection.ScalaReflectionMethodSignatureInferrer
import io.github.effiban.scala2java.spi.contexts.TermApplyInferenceContext
import io.github.effiban.scala2java.spi.entities.PartialDeclDef
import io.github.effiban.scala2java.spi.typeinferrers.ApplyDeclDefInferrer

import scala.annotation.tailrec
import scala.meta.{Term, Type, XtensionQuasiquoteTerm, XtensionQuasiquoteType}

trait InternalApplyDeclDefInferrer {
  def infer(termApply: Term.Apply, context: TermApplyInferenceContext): PartialDeclDef
}

private[typeinference] class InternalApplyDeclDefInferrerImpl(applyDeclDefInferrer: => ApplyDeclDefInferrer,
                                                              termSelectHasApplyMethod: TermSelectHasApplyMethod,
                                                              scalaReflectionMethodSignatureInferrer: ScalaReflectionMethodSignatureInferrer)
  extends InternalApplyDeclDefInferrer {

  @tailrec
  final def infer(termApply: Term.Apply, context: TermApplyInferenceContext): PartialDeclDef = {
    (termApply.fun, termApply.args) match {

        // TODO - is this needed? Should always run after desugaring
      case (termSelect: Term.Select, args) if termSelectHasApplyMethod(termSelect) =>
        val adjustedTermApply = Term.Apply(Term.Select(termSelect, q"apply"), args)
        infer(adjustedTermApply, context)

      // TODO - is this needed? Should always run after desugaring
      case (Term.ApplyType(termSelect: Term.Select, targs), args) if termSelectHasApplyMethod(termSelect) =>
        val adjustedTermApply = Term.Apply(
          Term.ApplyType(Term.Select(termSelect, q"apply"), targs),
          args
        )
        infer(adjustedTermApply, context)

      case _ =>
        applyDeclDefInferrer.infer(termApply, context) match {
          case partialDeclDef if partialDeclDef.isEmpty => inferByReflection(termApply, context)
          case other => other
        }
    }
  }

  private def inferByReflection(termApply: Term.Apply, context: TermApplyInferenceContext) = {
    import scalaReflectionMethodSignatureInferrer._

    val maybeParentType = context.maybeParentType
    val argTypes = context.maybeArgTypes.map(_.getOrElse(ScalaAny))

    (termApply.fun, maybeParentType) match {
      case (Term.Select(_, name), Some(parentType: Type.Ref)) => inferPartialMethodSignature(parentType, name, argTypes)
      case (Term.Select(qual: Term.Ref, name), None) => inferPartialMethodSignature(qual, name, argTypes)
      case _ => PartialDeclDef()
    }
  }
}