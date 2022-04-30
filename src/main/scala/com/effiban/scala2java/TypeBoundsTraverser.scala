package com.effiban.scala2java

import com.effiban.scala2java.JavaEmitter.{emit, emitComment}

import scala.meta.Type

trait TypeBoundsTraverser extends ScalaTreeTraverser[Type.Bounds]

private[scala2java] class TypeBoundsTraverserImpl(typeTraverser: => TypeTraverser) extends TypeBoundsTraverser {

  // Scala type bounds e.g. X <: Y
  override def traverse(typeBounds: Type.Bounds): Unit = {
    // Only upper or lower bounds allowed in Java, not both
    // TODO handle lower bound Null which can be skipped in Java
    (typeBounds.lo, typeBounds.hi) match {
      case (Some(lo), None) =>
        emit(" super ")
        typeTraverser.traverse(lo)
      case (None, Some(hi)) =>
        emit(" extends ")
        typeTraverser.traverse(hi)
      case (None, None) =>
      case _ =>
        // Both bounds provided - we can only emit a comment
        emitComment(typeBounds.toString)
    }
  }
}

object TypeBoundsTraverser extends TypeBoundsTraverserImpl(TypeTraverser)
