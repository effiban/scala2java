package effiban.scala2java.traversers

import effiban.scala2java.writers.JavaWriter

import scala.meta.Type

trait TypePlaceholderTraverser extends ScalaTreeTraverser[Type.Placeholder]

private[traversers] class TypePlaceholderTraverserImpl(typeBoundsTraverser: => TypeBoundsTraverser)
                                                      (implicit javaWriter: JavaWriter) extends TypePlaceholderTraverser {

  import javaWriter._

  // Underscore in type param, e.g. T[_]
  override def traverse(placeholderType: Type.Placeholder): Unit = {
    write("?")
    typeBoundsTraverser.traverse(placeholderType.bounds)
  }
}
