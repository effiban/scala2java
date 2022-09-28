package effiban.scala2java.classifiers

import scala.meta.Term

trait TermApplyClassifier {
  def isCollectionInitializer(termApply: Term.Apply): Boolean
}

private[classifiers] class TermApplyClassifierImpl(termNameClassifier: TermNameClassifier) extends TermApplyClassifier {

  override def isCollectionInitializer(termApply: Term.Apply): Boolean = termApply match {
    case Term.Apply(name: Term.Name, _) if termNameClassifier.isCollection(name) => true
    case Term.Apply(Term.ApplyType(name: Term.Name, _), _) if termNameClassifier.isCollection(name) => true
    case _ => false
  }
}

object TermApplyClassifier extends TermApplyClassifierImpl(TermNameClassifier)
