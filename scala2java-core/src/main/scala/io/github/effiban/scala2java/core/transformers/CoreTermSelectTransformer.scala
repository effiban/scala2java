package io.github.effiban.scala2java.core.transformers

import io.github.effiban.scala2java.core.classifiers.TermNameClassifier
import io.github.effiban.scala2java.core.entities.TermNameValues._
import io.github.effiban.scala2java.spi.transformers.TermSelectTransformer

import scala.meta.Term

private[transformers] class CoreTermSelectTransformer(termNameClassifier: TermNameClassifier) extends TermSelectTransformer {

  private final val TupleElementRegex = "_(\\d)".r

  // Transform a Scala-specific qualified name into an equivalent in Java
  // Either and Try use the syntax of the VAVR framework (Maven: io.vavr:vavr)
  override def transform(termSelect: Term.Select): Term.Select = {
    (termSelect.qual, termSelect.name) match {
      case (Term.Name(ScalaRange), Term.Name(Apply)) => Term.Select(Term.Name(JavaIntStream), Term.Name(JavaRange))
      case (Term.Name(ScalaRange), Term.Name(ScalaInclusive)) => Term.Select(Term.Name(JavaIntStream), Term.Name(JavaRangeClosed))

      case (Term.Name(ScalaOption), Term.Name(Apply)) => Term.Select(Term.Name(JavaOptional), Term.Name(JavaOfNullable))
      case (Term.Name(ScalaOption), Term.Name(Empty)) => Term.Select(Term.Name(JavaOptional), Term.Name(JavaAbsent))
      case (Term.Name(ScalaSome), Term.Name(Apply)) => Term.Select(Term.Name(JavaOptional), Term.Name(JavaOf))

      case (Term.Name(ScalaRight), Term.Name(Apply)) => Term.Select(Term.Name(Either), Term.Name(LowercaseRight))
      case (Term.Name(ScalaLeft), Term.Name(Apply)) => Term.Select(Term.Name(Either), Term.Name(LowercaseLeft))

      case (Term.Name(Try), Term.Name(Apply)) => Term.Select(Term.Name(Try), Term.Name(JavaOfSupplier))
      case (Term.Name(ScalaSuccess), Term.Name(Apply)) => Term.Select(Term.Name(Try), Term.Name(JavaSuccess))
      case (Term.Name(ScalaFailure), Term.Name(Apply)) => Term.Select(Term.Name(Try), Term.Name(JavaFailure))

      case (Term.Name(Future), Term.Name(Apply)) => Term.Select(Term.Name(JavaCompletableFuture), Term.Name(JavaSupplyAsync))
      case (Term.Name(Future), Term.Name(ScalaSuccessful)) => Term.Select(Term.Name(JavaCompletableFuture), Term.Name(JavaCompletedFuture))
      case (Term.Name(Future), Term.Name(ScalaFailed)) => Term.Select(Term.Name(JavaCompletableFuture), Term.Name(JavaFailedFuture))

      case(qual, Term.Name(TupleElementRegex(index))) => Term.Select(qual, Term.Name(s"v$index"))

      case (nm: Term.Name, Term.Name(Apply)) if termNameClassifier.isJavaStreamLike(nm) => Term.Select(Term.Name(Stream), Term.Name(JavaOf))
      case (nm: Term.Name, Term.Name(Apply) | Term.Name(Empty)) if termNameClassifier.isJavaListLike(nm) => Term.Select(Term.Name(List), Term.Name(JavaOf))
      case (nm: Term.Name, Term.Name(Apply) | Term.Name(Empty)) if termNameClassifier.isJavaSetLike(nm) => Term.Select(Term.Name(Set), Term.Name(JavaOf))
      case (nm: Term.Name, Term.Name(Apply)) if termNameClassifier.isJavaMapLike(nm) => Term.Select(Term.Name(Map), Term.Name(JavaOfEntries))
      case (nm: Term.Name, Term.Name(Empty)) if termNameClassifier.isJavaMapLike(nm) => Term.Select(Term.Name(Map), Term.Name(JavaOf))

      case _ => termSelect
    }
  }
}

object CoreTermSelectTransformer extends CoreTermSelectTransformer(TermNameClassifier)

