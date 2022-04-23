package com.effiban.scala2java

import com.effiban.scala2java.JavaEmitter.emit

import scala.meta.Name
import scala.meta.Term.This

object ThisTraverser extends ScalaTreeTraverser[This] {

  def traverse(`this`: This): Unit = {
    `this`.qual match {
      case Name.Anonymous() => emit("this")
      case name => NameTraverser.traverse(name)
    }
  }
}
