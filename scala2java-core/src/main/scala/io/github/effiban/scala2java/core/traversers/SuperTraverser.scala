package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.renderers.SuperRenderer

import scala.meta.Name
import scala.meta.Term.Super

trait SuperTraverser extends ScalaTreeTraverser[Super]

private[traversers] class SuperTraverserImpl(nameTraverser: NameTraverser, superRenderer: SuperRenderer) extends SuperTraverser {

  def traverse(`super`: Super): Unit = {
    val traversedSuper = `super`.thisp match {
      case Name.Anonymous() => `super`
      case thisp => `super`.copy(thisp = nameTraverser.traverse(thisp))
    }
    superRenderer.render(traversedSuper)
  }
}
