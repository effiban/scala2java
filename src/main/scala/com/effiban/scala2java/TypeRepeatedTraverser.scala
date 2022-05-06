package com.effiban.scala2java

import com.effiban.scala2java.JavaEmitter.emitEllipsis

import scala.meta.Type

trait TypeRepeatedTraverser extends ScalaTreeTraverser[Type.Repeated]

private[scala2java] class TypeRepeatedTraverserImpl(typeTraverser: => TypeTraverser)
                                                   (implicit javaEmitter: JavaEmitter) extends TypeRepeatedTraverser {

  // Vararg type,e.g.: T*
  override def traverse(repeatedType: Type.Repeated): Unit = {
    typeTraverser.traverse(repeatedType.tpe)
    emitEllipsis()
  }
}

object TypeRepeatedTraverser extends TypeRepeatedTraverserImpl(TypeTraverser)