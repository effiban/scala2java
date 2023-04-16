package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.renderers.TermPlaceholderRenderer

import scala.meta.Term

trait TermPlaceholderTraverser extends ScalaTreeTraverser[Term.Placeholder]

private[traversers] class TermPlaceholderTraverserImpl(termPlaceholderRenderer: TermPlaceholderRenderer) extends TermPlaceholderTraverser {

  // Underscore as expression - will compile in java only if it is an anonymous function, but rendering always
  override def traverse(termPlaceholder: Term.Placeholder): Unit = {
    termPlaceholderRenderer.render(termPlaceholder)
  }
}
