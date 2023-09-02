package io.github.effiban.scala2java.core.traversers

import scala.meta.Type

trait TypeProjectTraverser extends ScalaTreeTraverser1[Type.Project]

private[traversers] class TypeProjectTraverserImpl(typeTraverser: => TypeTraverser) extends TypeProjectTraverser {

  // A Scala type projection such as 'A#B' is a way to reference an inner type 'B' through an outer type 'A'.
  // In Java the equivalent is 'A.B' (but Java only has nested classes/interfaces, whereas Scala also has nested abstract types)
  override def traverse(typeProject: Type.Project): Type.Project = {
    val traversedQual = typeTraverser.traverse(typeProject.qual)
    Type.Project(traversedQual, typeProject.name)
  }
}
