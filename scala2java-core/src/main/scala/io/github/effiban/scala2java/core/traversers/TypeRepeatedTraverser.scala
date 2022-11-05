package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.Type

trait TypeRepeatedTraverser extends ScalaTreeTraverser[Type.Repeated]

private[traversers] class TypeRepeatedTraverserImpl(typeTraverser: => TypeTraverser)
                                                   (implicit javaWriter: JavaWriter) extends TypeRepeatedTraverser {

  import javaWriter._

  // Vararg type,e.g.: T*
  override def traverse(repeatedType: Type.Repeated): Unit = {
    typeTraverser.traverse(repeatedType.tpe)
    writeEllipsis()
  }
}
