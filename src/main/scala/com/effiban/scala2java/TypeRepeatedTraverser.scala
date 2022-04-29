package com.effiban.scala2java

import com.effiban.scala2java.JavaEmitter.emitEllipsis

import scala.meta.Type

trait TypeRepeatedTraverser extends ScalaTreeTraverser[Type.Repeated]

object TypeRepeatedTraverser extends TypeRepeatedTraverser {

  // Vararg type,e.g.: T*
  override def traverse(repeatedType: Type.Repeated): Unit = {
    TypeTraverser.traverse(repeatedType.tpe)
    emitEllipsis()
  }
}
