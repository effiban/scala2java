package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.renderers.TermNameRenderer

import scala.meta.Term

trait TermNameTraverser {
  def traverse(termName: Term.Name): Unit
}

private[traversers] class TermNameTraverserImpl(termNameWithoutRenderTraverser: => TermNameWithoutRenderTraverser,
                                                termNameRenderer: TermNameRenderer) extends TermNameTraverser {

  override def traverse(termName: Term.Name): Unit = {
    termNameWithoutRenderTraverser.traverse(termName) match {
      case Some(name: Term.Name) => termNameRenderer.render(name)
      case _ =>
    }
  }
}
