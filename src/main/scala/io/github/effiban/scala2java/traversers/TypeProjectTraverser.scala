package io.github.effiban.scala2java.traversers

import io.github.effiban.scala2java.writers.JavaWriter

import scala.meta.Type

trait TypeProjectTraverser extends ScalaTreeTraverser[Type.Project]

private[traversers] class TypeProjectTraverserImpl(typeTraverser: => TypeTraverser,
                                                   typeNameTraverser: => TypeNameTraverser)
                                                  (implicit javaWriter: JavaWriter) extends TypeProjectTraverser {

  import javaWriter._

  // A Scala type projection such as 'A#B' is a way to reference an inner type 'B' through an outer type 'A'.
  // In Java it would be 'A.B'
  override def traverse(typeProject: Type.Project): Unit = {
    typeTraverser.traverse(typeProject.qual)
    write(".")
    typeNameTraverser.traverse(typeProject.name)
  }
}
