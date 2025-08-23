package io.github.effiban.scala2java.core.typeinference

import io.github.effiban.scala2java.core.entities.TypeSelects.ScalaAny
import io.github.effiban.scala2java.core.factories.TermApplyInferenceContextFactory
import io.github.effiban.scala2java.core.predicates.TermSelectHasApplyMethod
import io.github.effiban.scala2java.core.reflection.ScalaReflectionMethodSignatureInferrer
import io.github.effiban.scala2java.spi.contexts.TermApplyInferenceContext
import io.github.effiban.scala2java.spi.entities.PartialDeclDef
import io.github.effiban.scala2java.spi.typeinferrers.ApplyDeclDefInferrer

import scala.annotation.tailrec
import scala.meta.{Term, Type, XtensionQuasiquoteTerm}

trait InternalApplyDeclDefInferrer {
  def infer(termApply: Term.Apply, context: TermApplyInferenceContext): PartialDeclDef
}

private[typeinference] class InternalApplyDeclDefInferrerImpl(applyDeclDefInferrer: => ApplyDeclDefInferrer,
                                                              termSelectHasApplyMethod: TermSelectHasApplyMethod,
                                                              scalaReflectionMethodSignatureInferrer: ScalaReflectionMethodSignatureInferrer,
                                                              termApplyInferenceContextFactory: => TermApplyInferenceContextFactory)
  extends InternalApplyDeclDefInferrer {

  @tailrec
  final def infer(termApply: Term.Apply, context: TermApplyInferenceContext): PartialDeclDef = {
    (termApply.fun, termApply.args) match {

      case (termSelect: Term.Select, args) if termSelectHasApplyMethod(termSelect) =>
        val adjustedTermApply = Term.Apply(Term.Select(termSelect, q"apply"), args)
        infer(adjustedTermApply, context)

      case (Term.ApplyType(termSelect: Term.Select, targs), args) if termSelectHasApplyMethod(termSelect) =>
        val adjustedTermApply = Term.Apply(
          Term.ApplyType(Term.Select(termSelect, q"apply"), targs),
          args
        )
        infer(adjustedTermApply, context)

      case _ =>
        val partialDeclDef = applyDeclDefInferrer.infer(termApply, context)
        partialDeclDef.maybeReturnType match {
          case Some(returnType) => partialDeclDef
          case None => inferByReflection(termApply, context)
        }
    }
  }

  @tailrec
  private def inferByReflection(termApply: Term.Apply, context: TermApplyInferenceContext): PartialDeclDef = {
    val partialDeclDef = inferByReflectionInner(termApply, context)
    (partialDeclDef, termApply) match {
      case (aPartialDeclDef, Term.Apply(innerTermApply: Term.Apply, args)) if aPartialDeclDef.isEmpty =>
        // If we could not infer, and the method invocation has a nested one - try the inner one with outer arg lists added to it
        val innerContextWithOuterArgLists = termApplyInferenceContextFactory.create(innerTermApply, context.maybeArgTypeLists)
        inferByReflection(innerTermApply, innerContextWithOuterArgLists)
      case _ => partialDeclDef
    }
  }

  private def inferByReflectionInner(termApply: Term.Apply, context: TermApplyInferenceContext) = {
    import scalaReflectionMethodSignatureInferrer._

    val maybeParentType = context.maybeParentType
    val argTypeLists = context.maybeArgTypeLists.map(_.map(_.getOrElse(ScalaAny)))

    (termApply.fun, maybeParentType) match {
      case (Term.Select(_, name), Some(parentType: Type.Ref)) => inferPartialMethodSignature(parentType, name, argTypeLists)
      case (Term.Select(qual: Term.Ref, name), None) => inferPartialMethodSignature(qual, name, argTypeLists)
      case _ => PartialDeclDef()
    }
  }
}