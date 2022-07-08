package effiban.scala2java.traversers

import effiban.scala2java.JavaEmitter

import scala.meta.Type

trait TypeProjectTraverser extends ScalaTreeTraverser[Type.Project]

private[scala2java] class TypeProjectTraverserImpl(typeTraverser: => TypeTraverser,
                                                   typeNameTraverser: => TypeNameTraverser)
                                                  (implicit javaEmitter: JavaEmitter) extends TypeProjectTraverser {

  import javaEmitter._

  // A Scala type projection such as 'A#B' is a way to reference an inner type 'B' through an outer type 'A'.
  // In Java it would be 'A.B'
  override def traverse(typeProject: Type.Project): Unit = {
    typeTraverser.traverse(typeProject.qual)
    emit(".")
    typeNameTraverser.traverse(typeProject.name)
  }
}

object TypeProjectTraverser extends TypeProjectTraverserImpl(TypeTraverser, TypeNameTraverser)