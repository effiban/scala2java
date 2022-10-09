package io.github.effiban.scala2java.classifiers

import scala.meta.Term.{Block, If, NewAnonymous, TryWithHandler, While}
import scala.meta.{Ctor, Defn, Stat, Term}

trait JavaStatClassifier {

  def requiresEndDelimiter(stat: Stat): Boolean
}

object JavaStatClassifier extends JavaStatClassifier {

  override def requiresEndDelimiter(stat: Stat): Boolean = {
    stat match {
      case _: Block |
           _: If |
           _: While |
           _: Term.Match |
           _: Term.Try |
           _: TryWithHandler |
           _: Term.Annotate |
           _: NewAnonymous |
           _: Term.PartialFunction |
           _: Ctor.Secondary |
           _: Defn.Def |
           _: Defn.Type |
           _: Defn.Class |
           _: Defn.Trait |
           _: Defn.Object => false
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
