package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.spi.transformers.TermNameTransformer

import scala.meta.Term

trait TermNameWithoutRenderTraverser {
  def traverse(termName: Term.Name): Option[Term.Name]
}

private[traversers] class TermNameWithoutRenderTraverserImpl(expressionTermTraverser: => ExpressionTermTraverser,
                                                             termNameTransformer: => TermNameTransformer)
  extends TermNameWithoutRenderTraverser {

  override def traverse(termName: Term.Name): Option[Term.Name] = {
    termNameTransformer.transform(termName) match {
      case Some(name: Term.Name) => Some(name)
      case Some(term: Term) =>
        expressionTermTraverser.traverse(term)
        None
      case None => Some(termName)
    }
  }
}
