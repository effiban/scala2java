package effiban.scala2java.classifiers

import scala.meta.Term
import scala.meta.Term.{Block, For, If, NewAnonymous, TryWithHandler, While}

trait JavaTermClassifier {

  def requiresStatementEnd(term: Term): Boolean
}

object JavaTermClassifier extends JavaTermClassifier {

  override def requiresStatementEnd(term: Term): Boolean = {
    term match {
      case _: Block |
           _: If |
           _: While |
           _: For |
           _: Term.Match |
           _: Term.Try |
           _: TryWithHandler |
           _: Term.Annotate |
           _: NewAnonymous |
           _: Term.PartialFunction => false
      case Term.Function(_, body) if hasMultipleStatements(body) => false
      case Term.AnonymousFunction(body) if hasMultipleStatements(body) => false
      case _ => true
    }
  }

  private def hasMultipleStatements(body: Term) = {
    body match {
      case Block(_ :: Nil) => false
      case Block(_) => true
      case _ => false
    }
  }
}
