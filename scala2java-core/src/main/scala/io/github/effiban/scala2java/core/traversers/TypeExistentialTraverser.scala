package io.github.effiban.scala2java.core.traversers

import scala.meta.Type

trait TypeExistentialTraverser extends ScalaTreeTraverser1[Type.Existential]

private[traversers] class TypeExistentialTraverserImpl(typeTraverser: => TypeTraverser) extends TypeExistentialTraverser {

  // type with existential constraint e.g.:  A[B] forSome {B <: Number with Serializable}
  override def traverse(existentialType: Type.Existential): Type.Existential = {
    //TODO - convert stats to Java for simple cases
    existentialType.copy(tpe = typeTraverser.traverse(existentialType.tpe))
  }
}
