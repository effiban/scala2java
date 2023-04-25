package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.renderers.ThisRenderer

import scala.meta.Name
import scala.meta.Term.This

trait ThisTraverser extends ScalaTreeTraverser[This]

private[traversers] class ThisTraverserImpl(nameTraverser: NameTraverser,
                                            thisRenderer: ThisRenderer) extends ThisTraverser {

  override def traverse(`this`: This): Unit = {
    val traversedThis = `this`.qual match {
      case Name.Anonymous() => `this`
      case qual => `this`.copy(qual = nameTraverser.traverse(qual))
    }
    thisRenderer.render(traversedThis)
  }
}
