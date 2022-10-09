package io.github.effiban.scala2java.traversers

import io.github.effiban.scala2java.writers.JavaWriter

import scala.meta.Type

trait TypeWildcardTraverser extends ScalaTreeTraverser[Type.Wildcard]

private[traversers] class TypeWildcardTraverserImpl(typeBoundsTraverser: => TypeBoundsTraverser)
                                                   (implicit javaWriter: JavaWriter) extends TypeWildcardTraverser {

  import javaWriter._

  // Underscore in type param, e.g. T[_] with possible bounds e.g. T[_ <: A]
  override def traverse(wildcardType: Type.Wildcard): Unit = {
    write("?")
    typeBoundsTraverser.traverse(wildcardType.bounds)
  }
}
