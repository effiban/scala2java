package io.github.effiban.scala2java.core.transformers

import io.github.effiban.scala2java.spi.transformers.DifferentTypeTransformer

trait CompositeDifferentTypeTransformer[I, O] extends DifferentTypeTransformer[I, O] {

  protected val transformers: List[DifferentTypeTransformer[I, O]]
  override def transform(obj: I): Option[O] =
    transformers.foldLeft[Option[O]](None)((maybeOutputObj, transformer) => maybeOutputObj.orElse(transformer.transform(obj)))
}
