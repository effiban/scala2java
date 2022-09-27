package effiban.scala2java.classifiers

import effiban.scala2java.entities.ScalaCollectionNames

import scala.meta.Term

trait TermApplyClassifier {

  def isCollectionInitializer(termApply: Term.Apply): Boolean
}

object TermApplyClassifier extends TermApplyClassifier {

  override def isCollectionInitializer(termApply: Term.Apply): Boolean = termApply match {
    case Term.Apply(Term.Name(name), _) if ScalaCollectionNames.isCollection(name) => true
    case Term.Apply(Term.ApplyType(Term.Name(name), _), _) if ScalaCollectionNames.isCollection(name) => true
    case _ => false
  }
}
