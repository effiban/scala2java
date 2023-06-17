package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.spi.transformers.TermNameTransformer

import scala.meta.Term

trait ExpressionTermNameTraverser extends ScalaTreeTraverser2[Term.Name, Term]

private[traversers] class ExpressionTermNameTraverserImpl(expressionTermTraverser: => ExpressionTermTraverser,
                                                          termNameTransformer: => TermNameTransformer) extends ExpressionTermNameTraverser {

  override def traverse(termName: Term.Name): Term = {
    termNameTransformer.transform(termName) match {
      case Some(transformedTermName: Term.Name) => transformedTermName
      case Some(term: Term) => expressionTermTraverser.traverse(term)
      case None => termName
    }
  }
}
