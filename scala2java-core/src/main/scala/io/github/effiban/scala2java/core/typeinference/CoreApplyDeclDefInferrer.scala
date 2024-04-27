package io.github.effiban.scala2java.core.typeinference

import io.github.effiban.scala2java.core.classifiers.TypeClassifier
import io.github.effiban.scala2java.core.entities.TypeSelects
import io.github.effiban.scala2java.spi.contexts.TermApplyInferenceContext
import io.github.effiban.scala2java.spi.entities.PartialDeclDef
import io.github.effiban.scala2java.spi.typeinferrers.ApplyDeclDefInferrer

import scala.meta.{Term, Type, XtensionQuasiquoteTerm}

private[typeinference] class CoreApplyDeclDefInferrer(initializerDeclDefInferrer: => InitializerDeclDefInferrer,
                                                      typeClassifier: TypeClassifier[Type]) extends ApplyDeclDefInferrer {

  override def infer(termApply: Term.Apply, context: TermApplyInferenceContext): PartialDeclDef = {

    val partialDeclDef = inferAsParameterizedInitializer(termApply, context)
    if (partialDeclDef.nonEmpty) {
      partialDeclDef
    } else {
      inferOther(termApply, context)
    }
  }

  private def inferAsParameterizedInitializer(termApply: Term.Apply, context: TermApplyInferenceContext) = {

    (termApply.fun, termApply.args) match {
      case (Term.ApplyType(Term.Select(qual: Term.Select, q"apply" | q"empty"), appliedTypes), args) =>
        inferByAppliedTypes(qual, appliedTypes, args.size)
      case (Term.ApplyType(Term.Select(qual@Term.Select(q"scala.concurrent", q"Future"), q"successful"), appliedTypes), args) =>
        inferByAppliedTypes(qual, appliedTypes, args.size)

      case (Term.Select(qual: Term.Select, q"apply" | q"empty"), _) => inferByArgTypes(qual, context.maybeArgTypes)
      case (Term.Select(qual@Term.Select(q"scala", q"Range"), q"inclusive"), _) => inferByArgTypes(qual, context.maybeArgTypes)
      case (Term.Select(qual@Term.Select(q"scala.concurrent", q"Future"), q"successful"), _) => inferByArgTypes(qual, context.maybeArgTypes)

      case _ => PartialDeclDef()
    }
  }

  private def inferByAppliedTypes(qual: Term.Select, appliedTypes: List[Type], size: Int) =
    initializerDeclDefInferrer.inferByAppliedTypes(qual, appliedTypes, size)

  private def inferByArgTypes(qual: Term.Select, maybeArgTypes: List[Option[Type]]) =
    initializerDeclDefInferrer.inferByArgTypes(qual, maybeArgTypes)

  private def inferOther(termApply: Term.Apply, context: TermApplyInferenceContext) = {
    val maybeReturnType = (termApply.fun, context) match {
      case (Term.ApplyType(Term.Select(q"scala.concurrent.Future", q"failed"), List(tpe)), _) =>
        Some(Type.Apply(TypeSelects.ScalaFuture, List(tpe)))

      case (Term.Select(q"scala.concurrent.Future", q"failed"), _) =>
        Some(Type.Apply(TypeSelects.ScalaFuture, List(TypeSelects.ScalaAny)))

      case (Term.Select(_, q"length"), TermApplyInferenceContext(Some(parentType), _)) if typeClassifier.isJavaListLike(parentType) =>
        Some(TypeSelects.ScalaInt)
      case (Term.Select(_, q"take"), TermApplyInferenceContext(Some(parentType), _)) if typeClassifier.isJavaListLike(parentType) =>
        Some(parentType)

      case (Term.Select(_, q"toString"), _) => Some(TypeSelects.ScalaString)

      case (q"scala.Predef.print" | q"scala.Predef.println", _) => Some(TypeSelects.ScalaUnit)

      // TODO add more
      case _ => None
    }

    PartialDeclDef(maybeParamTypes = context.maybeArgTypes, maybeReturnType = maybeReturnType)
  }
}