package com.effiban.scala2java

import com.effiban.scala2java.JavaEmitter.emit

import scala.meta.Name
import scala.meta.Term.Super

trait SuperTraverser extends ScalaTreeTraverser[Super]

object SuperTraverser extends SuperTraverser {

  def traverse(`super`: Super): Unit = {
    `super`.thisp match {
      case Name.Anonymous() =>
      case name =>
        NameTraverser.traverse(name)
        emit(".")
    }
    `super`.superp match {
      case Name.Anonymous() => emit("super")
      case name => NameTraverser.traverse(name)
    }
  }
}
