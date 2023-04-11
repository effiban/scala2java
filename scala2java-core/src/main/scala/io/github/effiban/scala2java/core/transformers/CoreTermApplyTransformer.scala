package io.github.effiban.scala2java.core.transformers

import io.github.effiban.scala2java.core.classifiers.TermNameClassifier
import io.github.effiban.scala2java.core.entities.TermNameValues._
import io.github.effiban.scala2java.spi.transformers.TermApplyTransformer

import scala.meta.Term

private[transformers] class CoreTermApplyTransformer(termNameClassifier: TermNameClassifier,
                                                     termSelectTermFunctionTransformer: => TermSelectTermFunctionTransformer) extends TermApplyTransformer {

  override final def transform(termApply: Term.Apply): Term.Apply = {
    termApply match {
      case Term.Apply(termSelect: Term.Select, args) =>
        transformQualifiedMethodName(termSelect)
          .map(transformedSelect => Term.Apply(transformedSelect, args))
          .getOrElse(termApply)

      case Term.Apply(Term.ApplyType(termSelect: Term.Select, targs), args) =>
        transformQualifiedMethodName(termSelect)
          .map(transformedSelect => Term.Apply(Term.ApplyType(transformedSelect, targs), args))
          .getOrElse(termApply)

      case _ => termApply
    }
  }

  // Transform a method name which is a Scala-specific qualified name, into an equivalent in Java
  // Either and Try use the syntax of the VAVR framework (Maven: io.vavr:vavr)
  private def transformQualifiedMethodName(termSelect: Term.Select): Option[Term] = {
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
      case (Term.Name(Future), Term.Name(ScalaSuccessful)) => Some(Term.Select(Term.Name(JavaCompletableFuture), Term.Name(JavaCompletedFuture)))
      case (Term.Name(Future), Term.Name(ScalaFailed)) => Some(Term.Select(Term.Name(JavaCompletableFuture), Term.Name(JavaFailedFuture)))

      case (nm: Term.Name, Term.Name(Apply) | Term.Name(Empty)) if termNameClassifier.isJavaStreamLike(nm) => Some(Term.Select(Term.Name(Stream), Term.Name(JavaOf)))
      case (nm: Term.Name, Term.Name(Apply) | Term.Name(Empty)) if termNameClassifier.isJavaListLike(nm) => Some(Term.Select(Term.Name(List), Term.Name(JavaOf)))
      case (nm: Term.Name, Term.Name(Apply) | Term.Name(Empty)) if termNameClassifier.isJavaSetLike(nm) => Some(Term.Select(Term.Name(Set), Term.Name(JavaOf)))
      case (nm: Term.Name, Term.Name(Apply)) if termNameClassifier.isJavaMapLike(nm) => Some(Term.Select(Term.Name(Map), Term.Name(JavaOfEntries)))
      case (nm: Term.Name, Term.Name(Empty)) if termNameClassifier.isJavaMapLike(nm) => Some(Term.Select(Term.Name(Map), Term.Name(JavaOf)))

      case (termFunction: Term.Function, methodName: Term.Name) => Some(termSelectTermFunctionTransformer.transform(termFunction, methodName))

      case _ => None
    }
  }
}
