package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.renderers.TermNameRenderer
import io.github.effiban.scala2java.core.transformers.InternalTermNameTransformer

import scala.meta.Term

trait TermNameTraverser {
  def traverse(termName: Term.Name): Unit
}

private[traversers] class TermNameTraverserImpl(termTraverser: => TermTraverser,
                                                termNameTransformer: => InternalTermNameTransformer,
                                                termNameRenderer: TermNameRenderer) extends TermNameTraverser {

  override def traverse(termName: Term.Name): Unit = {
    termNameTransformer.transform(termName) match {
      case name: Term.Name => termNameRenderer.render(name)
      case term: Term => termTraverser.traverse(term)
    }
  }
}
