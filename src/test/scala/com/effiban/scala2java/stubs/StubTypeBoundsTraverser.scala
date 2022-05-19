package com.effiban.scala2java.stubs

import com.effiban.scala2java.{JavaEmitter, TypeBoundsTraverser}

import scala.meta.Type

class StubTypeBoundsTraverser(implicit javaEmitter: JavaEmitter) extends TypeBoundsTraverser {
  import javaEmitter._

  override def traverse(typeBounds: Type.Bounds): Unit = {
    (typeBounds.lo, typeBounds.hi) match {
      case (Some(lo), None) =>
        emit(s" super $lo")
      case (None, Some(hi)) =>
        emit(s" extends $hi")
      case (None, None) =>
      case _ => emitComment(typeBounds.toString)
    }
  }
}
