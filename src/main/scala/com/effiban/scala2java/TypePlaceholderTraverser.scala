package com.effiban.scala2java

import com.effiban.scala2java.JavaEmitter.emit

import scala.meta.Type

object TypePlaceholderTraverser extends ScalaTreeTraverser[Type.Placeholder] {

  // Underscore in type param, e.g. T[_]
  override def traverse(placeholderType: Type.Placeholder): Unit = {
    emit("? ")
    GenericTreeTraverser.traverse(placeholderType.bounds)
  }

}
