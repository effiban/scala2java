package com.effiban.scala2java

import com.effiban.scala2java.JavaEmitter.{emit, emitComment}

import scala.meta.Type

object TypeBoundsTraverser extends ScalaTreeTraverser[Type.Bounds] {

  // Scala type bounds e.g. X <: Y
  override def traverse(typeBounds: Type.Bounds): Unit = {
    // Only upper or lower bounds allowed in Java, not both
    // TODO handle lower bound Null which can be skipped in Java
    (typeBounds.lo, typeBounds.hi) match {
      case (Some(lo), None) =>
        emit(" super ")
        TypeTraverser.traverse(lo)
      case (None, Some(hi)) =>
        emit(" extends ")
        TypeTraverser.traverse(hi)
      case (None, None) =>
      case _ =>
        // Both bounds provided - we can only emit a comment
        emitComment(typeBounds.toString)
    }
  }
}
