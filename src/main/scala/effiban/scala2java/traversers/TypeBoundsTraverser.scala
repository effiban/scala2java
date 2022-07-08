package effiban.scala2java.traversers

import effiban.scala2java.writers.JavaWriter

import scala.meta.Type

trait TypeBoundsTraverser extends ScalaTreeTraverser[Type.Bounds]

private[scala2java] class TypeBoundsTraverserImpl(typeTraverser: => TypeTraverser)
                                                 (implicit javaWriter: JavaWriter) extends TypeBoundsTraverser {

  import javaWriter._

  // Scala type bounds e.g. X <: Y
  override def traverse(typeBounds: Type.Bounds): Unit = {
    //Only upper or lower bounds allowed in Java, not both
    //TODO handle lower bound Null which can be skipped in Java
    (typeBounds.lo, typeBounds.hi) match {
      case (Some(lo), None) =>
        write(" super ")
        typeTraverser.traverse(lo)
      case (None, Some(hi)) =>
        write(" extends ")
        typeTraverser.traverse(hi)
      case (None, None) =>
      case _ =>
        // Both bounds provided - we can only write a comment
        writeComment(typeBounds.toString)
    }
  }
}

object TypeBoundsTraverser extends TypeBoundsTraverserImpl(TypeTraverser)
