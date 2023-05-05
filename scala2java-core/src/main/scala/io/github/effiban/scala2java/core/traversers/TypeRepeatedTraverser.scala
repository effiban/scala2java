package io.github.effiban.scala2java.core.traversers

import scala.meta.Type

trait TypeRepeatedTraverser extends ScalaTreeTraverser1[Type.Repeated]

private[traversers] class TypeRepeatedTraverserImpl(typeTraverser: => TypeTraverser) extends TypeRepeatedTraverser {

  // Vararg type,e.g.: T*
  override def traverse(repeatedType: Type.Repeated): Type.Repeated = {
    repeatedType.copy(tpe = typeTraverser.traverse(repeatedType.tpe))
  }
}
