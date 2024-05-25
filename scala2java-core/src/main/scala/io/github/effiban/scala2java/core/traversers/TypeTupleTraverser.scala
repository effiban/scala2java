package io.github.effiban.scala2java.core.traversers

import scala.meta.Type

trait TypeTupleTraverser extends ScalaTreeTraverser1[Type.Tuple]

private[traversers] class TypeTupleTraverserImpl(typeTraverser: => TypeTraverser)
  extends TypeTupleTraverser {

  //tuple as type, e.g. x: (Int, String).
  override def traverse(tupleType: Type.Tuple): Type.Tuple = {
    tupleType.copy(args = tupleType.args.map(typeTraverser.traverse))
  }
}
