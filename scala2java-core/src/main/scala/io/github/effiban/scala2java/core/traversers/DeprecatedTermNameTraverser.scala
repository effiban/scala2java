package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.renderers.TermNameRenderer

import scala.meta.Term

@deprecated
trait DeprecatedTermNameTraverser {
  def traverse(termName: Term.Name): Unit
}

@deprecated
private[traversers] class DeprecatedTermNameTraverserImpl(termNameWithoutRenderTraverser: => DeprecatedTermNameWithoutRenderTraverser,
                                                          termNameRenderer: TermNameRenderer) extends DeprecatedTermNameTraverser {

  override def traverse(termName: Term.Name): Unit = {
    termNameWithoutRenderTraverser.traverse(termName) match {
      case Some(name: Term.Name) => termNameRenderer.render(name)
      case _ =>
    }
  }
}
