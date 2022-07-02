package com.effiban.scala2java

import scala.meta.Type

trait TypeExistentialTraverser extends ScalaTreeTraverser[Type.Existential]

private[scala2java] class TypeExistentialTraverserImpl(typeTraverser: => TypeTraverser)
                                                      (implicit javaEmitter: JavaEmitter) extends TypeExistentialTraverser {

  import javaEmitter._

  // type with existential constraint e.g.:  A[B] forSome {B <: Number with Serializable}
  override def traverse(existentialType: Type.Existential): Unit = {
    typeTraverser.traverse(existentialType.tpe)
    //TODO - convert to Java for simple cases
    emitComment(s"forSome ${existentialType.stats.toString()}")
  }
}

object TypeExistentialTraverser extends TypeExistentialTraverserImpl(TypeTraverser)
