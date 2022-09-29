package effiban.scala2java.transformers

import effiban.scala2java.classifiers.TermNameClassifier

import scala.meta.Term

trait TermApplyNameTransformer {
  def transform(termName: Term.Name): Term
}

private[transformers] class TermApplyNameTransformerImpl(termNameClassifier: TermNameClassifier)
  extends TermApplyNameTransformer {

  import termNameClassifier._

  override def transform(termName: Term.Name): Term = termName match {
    case Term.Name("Range") => Term.Select(Term.Name("IntStream"), Term.Name("range"))
    case Term.Name("Option") => Term.Select(Term.Name("Optional"), Term.Name("ofNullable"))
    case Term.Name("Some") => Term.Select(Term.Name("Optional"), Term.Name("of"))
    // The next two transform to the VAVR framework syntax of 'Either' - only one I found in Java
    case Term.Name("Right") => Term.Select(Term.Name("Either"), Term.Name("right"))
    case Term.Name("Left") => Term.Select(Term.Name("Either"), Term.Name("left"))
    case Term.Name("Future") => Term.Select(Term.Name("CompletableFuture"), Term.Name("supplyAsync"))
    case nm if isJavaStreamLike(nm) => Term.Select(Term.Name("Stream"), Term.Name("of"))
    case nm if isJavaListLike(nm) => Term.Select(Term.Name("List"), Term.Name("of"))
    case nm if isJavaSetLike(nm) => Term.Select(Term.Name("Set"), Term.Name("of"))
    case nm if isJavaMapLike(nm) => Term.Select(Term.Name("Map"), Term.Name("ofEntries"))
    case nm => nm
  }
}

object TermApplyNameTransformer extends TermApplyNameTransformerImpl(TermNameClassifier)
