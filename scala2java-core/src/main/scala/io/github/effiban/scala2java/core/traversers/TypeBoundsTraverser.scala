package io.github.effiban.scala2java.core.traversers

import scala.meta.Type
import scala.meta.quasiquotes.XtensionQuasiquoteType

trait TypeBoundsTraverser extends ScalaTreeTraverser1[Type.Bounds]

private[traversers] class TypeBoundsTraverserImpl(typeTraverser: => TypeTraverser) extends TypeBoundsTraverser {

  // Scala type bounds e.g. X <: Y
  override def traverse(typeBounds: Type.Bounds): Type.Bounds = {
    //TODO - call the traverser with an argument indicating that Java primitives should be boxed
    val traversedMaybeLo = typeBounds.lo match {
      case Some(t"Null") | None => None
      case Some(lo) => Some(typeTraverser.traverse(lo))
    }
    val traversedMaybeHi = typeBounds.hi.map(typeTraverser.traverse)
    Type.Bounds(lo = traversedMaybeLo, hi = traversedMaybeHi)
  }
}
