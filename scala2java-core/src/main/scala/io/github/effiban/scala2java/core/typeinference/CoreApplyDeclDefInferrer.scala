package io.github.effiban.scala2java.core.typeinference

import io.github.effiban.scala2java.core.classifiers.TypeClassifier
import io.github.effiban.scala2java.core.entities.TermNameValues.{Empty, Print, Println, ScalaFailed, ScalaInclusive, ScalaRange, ScalaSuccessful}
import io.github.effiban.scala2java.core.entities.{TermNameValues, TypeSelects}
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
      case (Term.ApplyType(Term.Select(qual: Term.Select, Term.Name(TermNameValues.Apply) | Term.Name(Empty)), appliedTypes), args) =>
        inferByAppliedTypes(qual, appliedTypes, args.size)
      case (Term.ApplyType(Term.Select(qual@Term.Select(q"scala.concurrent", q"Future"), Term.Name(ScalaSuccessful)), appliedTypes), args) =>
        inferByAppliedTypes(qual, appliedTypes, args.size)

      case (Term.Select(qual: Term.Select, Term.Name(TermNameValues.Apply) | Term.Name(Empty)), _) => inferByArgTypes(qual, context.maybeArgTypes)
      case (Term.Select(qual@Term.Select(q"scala", q"Range"), Term.Name(ScalaInclusive)), _) => inferByArgTypes(qual, context.maybeArgTypes)
      case (Term.Select(qual@Term.Select(q"scala.concurrent", q"Future"), Term.Name(ScalaSuccessful)), _) => inferByArgTypes(qual, context.maybeArgTypes)

      // TODO remove the block below once core term names are fully qualified at the start
      case (Term.ApplyType(Term.Select(name: Term.Name, Term.Name(TermNameValues.Apply) | Term.Name(Empty)), appliedTypes), args) =>
        inferByAppliedTypes(name, appliedTypes, args.size)
      case (Term.ApplyType(Term.Select(name@Term.Name(TermNameValues.Future), Term.Name(ScalaSuccessful)), appliedTypes), args) =>
        inferByAppliedTypes(name, appliedTypes, args.size)

      case (Term.Select(name: Term.Name, Term.Name(TermNameValues.Apply) | Term.Name(Empty)), _) => inferByArgTypes(name, context.maybeArgTypes)
      case (Term.Select(name@Term.Name(ScalaRange), Term.Name(ScalaInclusive)), _) => inferByArgTypes(name, context.maybeArgTypes)
      case (Term.Select(name@Term.Name(TermNameValues.Future), Term.Name(ScalaSuccessful)), _) => inferByArgTypes(name, context.maybeArgTypes)

      case _ => PartialDeclDef()
    }
  }

  private def inferByAppliedTypes(qual: Term.Select, appliedTypes: List[Type], size: Int) =
    initializerDeclDefInferrer.inferByAppliedTypes(qual, appliedTypes, size)

  @deprecated
  private def inferByAppliedTypes(name: Term.Name, appliedTypes: List[Type], size: Int) =
    initializerDeclDefInferrer.inferByAppliedTypes(name, appliedTypes, size)

  private def inferByArgTypes(qual: Term.Select, maybeArgTypes: List[Option[Type]]) =
    initializerDeclDefInferrer.inferByArgTypes(qual, maybeArgTypes)

  @deprecated
  private def inferByArgTypes(name: Term.Name, maybeArgTypes: List[Option[Type]]) =
    initializerDeclDefInferrer.inferByArgTypes(name, maybeArgTypes)

  private def inferOther(termApply: Term.Apply, context: TermApplyInferenceContext) = {
    val maybeReturnType = (termApply.fun, context) match {
      case (Term.ApplyType(Term.Select(q"scala.concurrent.Future", Term.Name(ScalaFailed)), List(tpe)), _) =>
        Some(Type.Apply(TypeSelects.ScalaFuture, List(tpe)))

        // TODO - remove with the rest
      case (Term.ApplyType(Term.Select(Term.Name(TermNameValues.Future), Term.Name(ScalaFailed)), List(tpe)), _) =>
        Some(Type.Apply(TypeSelects.ScalaFuture, List(tpe)))

      case (Term.Select(q"scala.concurrent.Future", Term.Name(ScalaFailed)), _) =>
        Some(Type.Apply(TypeSelects.ScalaFuture, List(TypeSelects.ScalaAny)))

        // TODO - remove with the rest
      case (Term.Select(Term.Name(TermNameValues.Future), Term.Name(ScalaFailed)), _) =>
        Some(Type.Apply(TypeSelects.ScalaFuture, List(TypeSelects.ScalaAny)))

      case (Term.Select(_, q"length"), TermApplyInferenceContext(Some(parentType), _)) if typeClassifier.isJavaListLike(parentType) =>
        Some(TypeSelects.ScalaInt)
      case (Term.Select(_, q"take"), TermApplyInferenceContext(Some(parentType), _)) if typeClassifier.isJavaListLike(parentType) =>
        Some(parentType)

      case (Term.Select(_, q"toString"), _) => Some(TypeSelects.ScalaString)

      case (q"scala.Predef.print" | q"scala.Predef.println", _) => Some(TypeSelects.ScalaUnit)

      // TODO - remove with the rest
      case (Term.Name(Print) | Term.Name(Println), _) => Some(TypeSelects.ScalaUnit)

      // TODO add more
      case _ => None
    }

    PartialDeclDef(maybeParamTypes = context.maybeArgTypes, maybeReturnType = maybeReturnType)
  }
}