package com.effiban.scala2java

import scala.meta.Name
import scala.meta.Term.Super

trait SuperTraverser extends ScalaTreeTraverser[Super]

private[scala2java] class SuperTraverserImpl(nameTraverser: => NameTraverser)
                                            (implicit javaEmitter: JavaEmitter) extends SuperTraverser {

  import javaEmitter._

  def traverse(`super`: Super): Unit = {
    `super`.thisp match {
      case Name.Anonymous() =>
      case name =>
        nameTraverser.traverse(name)
        emit(".")
    }
    emit("super")
    `super`.superp match {
      case Name.Anonymous() =>
      case name => emitComment(s"extends ${name.value}")
    }
  }
}

object SuperTraverser extends SuperTraverserImpl(NameTraverser)
