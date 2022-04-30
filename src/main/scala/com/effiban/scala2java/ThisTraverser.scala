package com.effiban.scala2java

import com.effiban.scala2java.JavaEmitter.emit

import scala.meta.Name
import scala.meta.Term.This

trait ThisTraverser extends ScalaTreeTraverser[This]

private[scala2java] class ThisTraverserImpl(nameTraverser: => NameTraverser) extends ThisTraverser {

  override def traverse(`this`: This): Unit = {
    `this`.qual match {
      case Name.Anonymous() => emit("this")
      case name => nameTraverser.traverse(name)
    }
  }
}

object ThisTraverser extends ThisTraverserImpl(NameTraverser)
