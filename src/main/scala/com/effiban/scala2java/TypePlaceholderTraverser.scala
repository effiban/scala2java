package com.effiban.scala2java

import scala.meta.Type

trait TypePlaceholderTraverser extends ScalaTreeTraverser[Type.Placeholder]

private[scala2java] class TypePlaceholderTraverserImpl(typeBoundsTraverser: => TypeBoundsTraverser)
                                                      (implicit javaEmitter: JavaEmitter) extends TypePlaceholderTraverser {

  import javaEmitter._

  // Underscore in type param, e.g. T[_]
  override def traverse(placeholderType: Type.Placeholder): Unit = {
    emit("? ")
    typeBoundsTraverser.traverse(placeholderType.bounds)
  }

}

object TypePlaceholderTraverser extends TypePlaceholderTraverserImpl(TypeBoundsTraverser)