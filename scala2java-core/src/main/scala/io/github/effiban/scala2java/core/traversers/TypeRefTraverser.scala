package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.Type

trait TypeRefTraverser extends ScalaTreeTraverser[Type.Ref]

private[traversers] class TypeRefTraverserImpl(typeNameTraverser: => TypeNameTraverser,
                                               typeSelectTraverser: => TypeSelectTraverser,
                                               typeProjectTraverser: => TypeProjectTraverser,
                                               typeSingletonTraverser: => TypeSingletonTraverser)
                                              (implicit javaWriter: JavaWriter) extends TypeRefTraverser {

  import javaWriter._

  override def traverse(typeRef: Type.Ref): Unit = typeRef match {
    case typeName: Type.Name => typeNameTraverser.traverse(typeName)
    case typeSelect: Type.Select => typeSelectTraverser.traverse(typeSelect)
    case typeProject: Type.Project => typeProjectTraverser.traverse(typeProject)
    case typeSingleton: Type.Singleton => typeSingletonTraverser.traverse(typeSingleton)
    case _ => writeComment(s"UNSUPPORTED: $typeRef")
  }
}
