package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.Type

// A traverser for the special expression 'classOf[T]'
trait ClassOfTraverser {
  def traverse(typeArgs: List[Type]): Unit
}

private[traversers] class ClassOfTraverserImpl(typeTraverser: => TypeTraverser)
                                              (implicit javaWriter: JavaWriter) extends ClassOfTraverser {
  import javaWriter._

  override def traverse(typeArgs: List[Type]): Unit =
    typeArgs match {
      case arg :: Nil =>
        typeTraverser.traverse(arg)
        write(".class")
      case _ => write(s"UNPARSEABLE 'classOf' with types: ${if (typeArgs.nonEmpty) {typeArgs.mkString(", ")} else "(none)"}")
  }
}