package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.transformers.TypeTupleToTypeApplyTransformer

import scala.meta.Type

trait TypeTupleTraverser extends ScalaTreeTraverser2[Type.Tuple, Type.Apply]

private[traversers] class TypeTupleTraverserImpl(typeApplyTraverser: => TypeApplyTraverser,
                                                 typeTupleToTypeApplyTransformer: TypeTupleToTypeApplyTransformer)
  extends TypeTupleTraverser {

  //tuple as type, e.g. x: (Int, String).
  override def traverse(tupleType: Type.Tuple): Type.Apply = {
    typeApplyTraverser.traverse(typeTupleToTypeApplyTransformer.transform(tupleType))
  }
}
