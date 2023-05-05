package io.github.effiban.scala2java.core.traversers

import scala.meta.Type

trait TypeRefineTraverser extends ScalaTreeTraverser1[Type.Refine]

private[traversers] class TypeRefineTraverserImpl(typeTraverser: => TypeTraverser) extends TypeRefineTraverser {

  // Scala feature which allows to extend the definition of a type, e.g. the block in the RHS below:
  // type B = A {def f: Int}
  override def traverse(refinedType: Type.Refine): Type.Refine = {
    //TODO maybe convert stats to Java inheritance
    refinedType.copy(tpe = refinedType.tpe.map(typeTraverser.traverse))
  }
}
