package com.effiban.scala2java

import com.effiban.scala2java.JavaEmitter.emitComment

import scala.meta.Type

trait TypeExistentialTraverser extends ScalaTreeTraverser[Type.Existential]

object TypeExistentialTraverser extends TypeExistentialTraverser {

  // type with existential constraint e.g.:  A[B] forSome {B <: Number with Serializable}
  override def traverse(existentialType: Type.Existential): Unit = {
    TypeTraverser.traverse(existentialType.tpe)
    // TODO - convert to Java for simple cases
    emitComment(existentialType.stats.toString())
  }
}
