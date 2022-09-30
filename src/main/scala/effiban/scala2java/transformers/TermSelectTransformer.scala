package effiban.scala2java.transformers

import effiban.scala2java.classifiers.TermNameClassifier
import effiban.scala2java.entities.TermNameValues
import effiban.scala2java.entities.TermNameValues._

import scala.meta.Term

trait TermSelectTransformer {
  def transform(termSelect: Term.Select): Term.Select
}

class TermSelectTransformerImpl(termNameClassifier: TermNameClassifier) extends TermSelectTransformer {

  // Transform a Scala-specific qualified name into an equivalent in Java
  override def transform(termSelect: Term.Select): Term.Select = {
    (termSelect.qual, termSelect.name) match {
      case (Term.Name(ScalaRange), Term.Name(ScalaApply)) => Term.Select(Term.Name(JavaIntStream), Term.Name(JavaRange))
      case (Term.Name(ScalaRange), Term.Name(ScalaInclusive)) => Term.Select(Term.Name(JavaIntStream), Term.Name(JavaRangeClosed))

      case (Term.Name(ScalaOption), Term.Name(ScalaApply)) => Term.Select(Term.Name(JavaOptional), Term.Name(JavaOfNullable))
      case (Term.Name(ScalaSome), Term.Name(ScalaApply)) => Term.Select(Term.Name(JavaOptional), Term.Name(JavaOf))

      // The next two transform to the VAVR framework syntax of 'Either' - only one I found for Java
      case (Term.Name(ScalaRight), Term.Name(ScalaApply)) => Term.Select(Term.Name(TermNameValues.Either), Term.Name(LowercaseRight))
      case (Term.Name(ScalaLeft), Term.Name(ScalaApply)) => Term.Select(Term.Name(TermNameValues.Either), Term.Name(LowercaseLeft))

      case (Term.Name(Future), Term.Name(ScalaApply)) => Term.Select(Term.Name(JavaCompletableFuture), Term.Name(JavaSupplyAsync))
      case (Term.Name(Future), Term.Name(ScalaSuccessful)) => Term.Select(Term.Name(JavaCompletableFuture), Term.Name(JavaCompletedFuture))
      case (Term.Name(Future), Term.Name(ScalaFailed)) => Term.Select(Term.Name(JavaCompletableFuture), Term.Name(JavaFailedFuture))

      case (nm: Term.Name, Term.Name(ScalaApply)) if termNameClassifier.isJavaStreamLike(nm) => Term.Select(Term.Name(TermNameValues.Stream), Term.Name(JavaOf))
      case (nm: Term.Name, Term.Name(ScalaApply)) if termNameClassifier.isJavaListLike(nm) => Term.Select(Term.Name(TermNameValues.List), Term.Name(JavaOf))
      case (nm: Term.Name, Term.Name(ScalaApply)) if termNameClassifier.isJavaSetLike(nm) => Term.Select(Term.Name(TermNameValues.Set), Term.Name(JavaOf))
      case (nm: Term.Name, Term.Name(ScalaApply)) if termNameClassifier.isJavaMapLike(nm) => Term.Select(Term.Name(TermNameValues.Map), Term.Name(JavaOfEntries))

      case _ => termSelect
    }
  }
}

object TermSelectTransformer extends TermSelectTransformerImpl(TermNameClassifier)
