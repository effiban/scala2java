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
    case nm if isJavaStreamLike(nm) => Term.Select(Term.Name("Stream"), Term.Name("of"))
    case nm if isJavaListLike(nm) => Term.Select(Term.Name("List"), Term.Name("of"))
    case nm if isJavaSetLike(nm) => Term.Select(Term.Name("Set"), Term.Name("of"))
    case nm if isJavaMapLike(nm) => Term.Select(Term.Name("Map"), Term.Name("ofEntries"))
    case nm => nm
  }
}

object TermApplyNameTransformer extends TermApplyNameTransformerImpl(TermNameClassifier)
