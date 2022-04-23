package com.effiban.scala2java

import com.effiban.scala2java.JavaEmitter.{emit, emitComment}

import scala.meta.Type

object TypeBoundsTraverser extends ScalaTreeTraverser[Type.Bounds] {

  // Scala type bounds e.g. X <: Y
  override def traverse(typeBounds: Type.Bounds): Unit = {
    // Only upper or lower bounds allowed in Java, not both - but if a Scala lower bound is `Null` it can be skipped
    (typeBounds.lo, typeBounds.hi) match {
      case (Some(lo), None) =>
        emit(" super ")
        GenericTreeTraverser.traverse(lo)
      case (None, Some(hi)) =>
        emit(" extends ")
        GenericTreeTraverser.traverse(hi)
      // TODO handle lower bound Null
      case _ => emitComment(typeBounds.toString)
    }
  }
}
