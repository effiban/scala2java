package io.github.effiban.scala2java.core.traversers

import scala.meta.Name
import scala.meta.Term.Super

trait SuperTraverser extends ScalaTreeTraverser1[Super]

private[traversers] class SuperTraverserImpl(nameTraverser: NameTraverser) extends SuperTraverser {

  def traverse(`super`: Super): Super = {
    `super`.thisp match {
      case Name.Anonymous() => `super`
      case thisp => `super`.copy(thisp = nameTraverser.traverse(thisp))
    }
  }
}
