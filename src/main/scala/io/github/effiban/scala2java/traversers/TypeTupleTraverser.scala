package io.github.effiban.scala2java.traversers

import io.github.effiban.scala2java.transformers.TypeTupleToTypeApplyTransformer

import scala.meta.Type

trait TypeTupleTraverser extends ScalaTreeTraverser[Type.Tuple]

private[traversers] class TypeTupleTraverserImpl(typeApplyTraverser: => TypeApplyTraverser,
                                                 typeTupleToTypeApplyTransformer: TypeTupleToTypeApplyTransformer)
  extends TypeTupleTraverser {

  //tuple as type, e.g. x: (Int, String).
  override def traverse(tupleType: Type.Tuple): Unit = {
    typeApplyTraverser.traverse(typeTupleToTypeApplyTransformer.transform(tupleType))
  }
}
