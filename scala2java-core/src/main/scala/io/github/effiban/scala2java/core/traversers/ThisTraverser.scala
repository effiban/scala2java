package io.github.effiban.scala2java.core.traversers

import scala.meta.Name
import scala.meta.Term.This

trait ThisTraverser extends ScalaTreeTraverser1[This]

private[traversers] class ThisTraverserImpl(nameTraverser: NameTraverser) extends ThisTraverser {

  override def traverse(`this`: This): This = {
    `this`.qual match {
      case Name.Anonymous() => `this`
      case qual => `this`.copy(qual = nameTraverser.traverse(qual))
    }
  }
}
