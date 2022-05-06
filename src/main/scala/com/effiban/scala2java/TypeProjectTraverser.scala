package com.effiban.scala2java

import scala.meta.Type

trait TypeProjectTraverser extends ScalaTreeTraverser[Type.Project]

private[scala2java] class TypeProjectTraverserImpl(typeTraverser: => TypeTraverser,
                                                   typeNameTraverser: => TypeNameTraverser)
                                                  (implicit javaEmitter: JavaEmitter) extends TypeProjectTraverser {

  import javaEmitter._

  // A scala type projecting expression like: a#B
  override def traverse(typeProject: Type.Project): Unit = {
    typeTraverser.traverse(typeProject.qual)
    emit(".")
    typeNameTraverser.traverse(typeProject.name)
  }
}

object TypeProjectTraverser extends TypeProjectTraverserImpl(TypeTraverser, TypeNameTraverser)