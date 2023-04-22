package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.transformers.InternalTermNameTransformer

import scala.meta.Term

trait TermNameWithoutRenderTraverser {
  def traverse(termName: Term.Name): Option[Term.Name]
}

private[traversers] class TermNameWithoutRenderTraverserImpl(termTraverser: => TermTraverser,
                                                             termNameTransformer: => InternalTermNameTransformer)
  extends TermNameWithoutRenderTraverser {

  override def traverse(termName: Term.Name): Option[Term.Name] = {
    termNameTransformer.transform(termName) match {
      case name: Term.Name => Some(name)
      case term: Term =>
        termTraverser.traverse(term)
        None
    }
  }
}
