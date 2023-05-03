package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.renderers.TypeNameRenderer
import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.Type

trait TypeProjectTraverser extends ScalaTreeTraverser[Type.Project]

private[traversers] class TypeProjectTraverserImpl(typeTraverser: => TypeTraverser,
                                                   typeNameTraverser: TypeNameTraverser,
                                                   typeNameRenderer: TypeNameRenderer)
                                                  (implicit javaWriter: JavaWriter) extends TypeProjectTraverser {

  import javaWriter._

  // A Scala type projection such as 'A#B' is a way to reference an inner type 'B' through an outer type 'A'.
  // In Java the equivalent is 'A.B' (but Java only has nested classes/interfaces, whereas Scala also has nested abstract types)
  override def traverse(typeProject: Type.Project): Unit = {
    typeTraverser.traverse(typeProject.qual)
    writeQualifierSeparator()
    val traversedTypeName = typeNameTraverser.traverse(typeProject.name)
    typeNameRenderer.render(traversedTypeName)
  }
}
