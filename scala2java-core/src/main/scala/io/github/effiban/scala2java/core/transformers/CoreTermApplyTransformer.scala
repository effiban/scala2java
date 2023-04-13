package io.github.effiban.scala2java.core.transformers

import io.github.effiban.scala2java.core.classifiers.{TermNameClassifier, TypeClassifier}
import io.github.effiban.scala2java.core.entities.TermNameValues
import io.github.effiban.scala2java.core.entities.TermNameValues.{List => _, _}
import io.github.effiban.scala2java.spi.contexts.TermApplyTransformationContext
import io.github.effiban.scala2java.spi.transformers.TermApplyTransformer

import scala.meta.{Term, Type, XtensionQuasiquoteTerm}

private[transformers] class CoreTermApplyTransformer(termNameClassifier: TermNameClassifier,
                                                     typeClassifier: TypeClassifier[Type],
                                                     termSelectTermFunctionTransformer: => TermSelectTermFunctionTransformer) extends TermApplyTransformer {

  override final def transform(termApply: Term.Apply, context: TermApplyTransformationContext = TermApplyTransformationContext()): Term.Apply =
    transformOptional(termApply, context).getOrElse(termApply)

  private def transformOptional(termApply: Term.Apply, context: TermApplyTransformationContext) = {
    termApply match {
      case Term.Apply(termSelect: Term.Select, args) =>
        transformUntypedByQualifiedName(termSelect, args)
          .orElse(transformUntypedByQualifierTypeAndName(termSelect, args, context))

      case Term.Apply(Term.ApplyType(termSelect: Term.Select, targs), args) =>
        transformTypedByQualifiedName(termSelect, targs, args)
          .orElse(transformTypedByQualifierTypeAndName(termSelect, targs, args, context))

      case _ => None
    }
  }

  private def transformUntypedByQualifiedName(termSelect: Term.Select, args: List[Term]) = {
    transformByQualifiedName(termSelect)
      .map(transformedSelect => Term.Apply(transformedSelect, args))
  }

  private def transformTypedByQualifiedName(termSelect: Term.Select, targs: List[Type], args: List[Term]) = {
    transformByQualifiedName(termSelect)
      .map(transformedSelect => Term.Apply(Term.ApplyType(transformedSelect, targs), args))
  }

  // Transform a method name which is a Scala-specific qualified name, into an equivalent in Java
  // Either and Try use the syntax of the VAVR framework (Maven: io.vavr:vavr)
  private def transformByQualifiedName(termSelect: Term.Select): Option[Term] = {
    (termSelect.qual, termSelect.name) match {
      case (Term.Name(ScalaRange), Term.Name(Apply)) => Some(Term.Select(Term.Name(JavaIntStream), Term.Name(JavaRange)))
      case (Term.Name(ScalaRange), Term.Name(ScalaInclusive)) => Some(Term.Select(Term.Name(JavaIntStream), Term.Name(JavaRangeClosed)))

      case (Term.Name(ScalaOption), Term.Name(Apply)) => Some(Term.Select(Term.Name(JavaOptional), Term.Name(JavaOfNullable)))
      case (Term.Name(ScalaOption), Term.Name(Empty)) => Some(Term.Select(Term.Name(JavaOptional), Term.Name(JavaAbsent)))
      case (Term.Name(ScalaSome), Term.Name(Apply)) => Some(Term.Select(Term.Name(JavaOptional), Term.Name(JavaOf)))

      case (Term.Name(ScalaRight), Term.Name(Apply)) => Some(Term.Select(Term.Name(Either), Term.Name(LowercaseRight)))
      case (Term.Name(ScalaLeft), Term.Name(Apply)) => Some(Term.Select(Term.Name(Either), Term.Name(LowercaseLeft)))

      case (Term.Name(Try), Term.Name(Apply)) => Some(Term.Select(Term.Name(Try), Term.Name(JavaOfSupplier)))
      case (Term.Name(ScalaSuccess), Term.Name(Apply)) => Some(Term.Select(Term.Name(Try), Term.Name(JavaSuccess)))
      case (Term.Name(ScalaFailure), Term.Name(Apply)) => Some(Term.Select(Term.Name(Try), Term.Name(JavaFailure)))

      case (Term.Name(Future), Term.Name(Apply)) => Some(Term.Select(Term.Name(JavaCompletableFuture), Term.Name(JavaSupplyAsync)))
      case (Term.Name(Future), Term.Name(ScalaSuccessful)) =>
        Some(Term.Select(Term.Name(JavaCompletableFuture), Term.Name(JavaCompletedFuture)))
      case (Term.Name(Future), Term.Name(ScalaFailed)) => Some(Term.Select(Term.Name(JavaCompletableFuture), Term.Name(JavaFailedFuture)))

      case (nm: Term.Name, Term.Name(Apply) | Term.Name(Empty)) if termNameClassifier.isJavaStreamLike(nm) =>
        Some(Term.Select(Term.Name(Stream), Term.Name(JavaOf)))
      case (nm: Term.Name, Term.Name(Apply) | Term.Name(Empty)) if termNameClassifier.isJavaListLike(nm) =>
        Some(Term.Select(Term.Name(TermNameValues.List), Term.Name(JavaOf)))
      case (nm: Term.Name, Term.Name(Apply) | Term.Name(Empty)) if termNameClassifier.isJavaSetLike(nm) =>
        Some(Term.Select(Term.Name(Set), Term.Name(JavaOf)))
      case (nm: Term.Name, Term.Name(Apply)) if termNameClassifier.isJavaMapLike(nm) =>
        Some(Term.Select(Term.Name(Map), Term.Name(JavaOfEntries)))
      case (nm: Term.Name, Term.Name(Empty)) if termNameClassifier.isJavaMapLike(nm) =>
        Some(Term.Select(Term.Name(Map), Term.Name(JavaOf)))

      case (termFunction: Term.Function, methodName: Term.Name) =>
        Some(termSelectTermFunctionTransformer.transform(termFunction, methodName))

      case _ => None
    }
  }

  private def transformUntypedByQualifierTypeAndName(termSelect: Term.Select,
                                                     args: List[Term],
                                                     context: TermApplyTransformationContext): Option[Term.Apply] = {
    transformByQualifierTypeAndName(termSelect, args, context)
      .map { case (transformedSelect, transformedArgs) => Term.Apply(transformedSelect, transformedArgs) }
  }

  private def transformTypedByQualifierTypeAndName(termSelect: Term.Select,
                                                   targs: List[Type],
                                                   args: List[Term],
                                                   context: TermApplyTransformationContext): Option[Term.Apply] = {
    transformByQualifierTypeAndName(termSelect, args, context)
      .map { case (transformedSelect, transformedArgs) => Term.Apply(Term.ApplyType(transformedSelect, targs), transformedArgs) }
  }

  private def transformByQualifierTypeAndName(termSelect: Term.Select,
                                              args: List[Term],
                                              context: TermApplyTransformationContext): Option[(Term.Select, List[Term])] = {
    (context.maybeParentType, termSelect.name, args) match {
      case (Some(parentType), q"take", arg :: Nil) if typeClassifier.isJavaListLike(parentType) =>
        Some(termSelect.copy(name = q"subList"), List(q"0", arg))
      case _ => None
    }
  }
}
