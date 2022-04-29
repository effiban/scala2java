package com.effiban.scala2java

import com.effiban.scala2java.JavaEmitter.emit

import scala.meta.Type

trait TypePlaceholderTraverser extends ScalaTreeTraverser[Type.Placeholder]

object TypePlaceholderTraverser extends TypePlaceholderTraverser {

  // Underscore in type param, e.g. T[_]
  override def traverse(placeholderType: Type.Placeholder): Unit = {
    emit("? ")
    TypeBoundsTraverser.traverse(placeholderType.bounds)
  }

}
