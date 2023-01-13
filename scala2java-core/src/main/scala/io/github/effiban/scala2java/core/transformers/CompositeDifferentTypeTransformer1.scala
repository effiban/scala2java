package io.github.effiban.scala2java.core.transformers

import io.github.effiban.scala2java.spi.transformers.DifferentTypeTransformer1

trait CompositeDifferentTypeTransformer1[I, A, O] extends DifferentTypeTransformer1[I, A, O] {

  protected val transformers: List[DifferentTypeTransformer1[I, A, O]]

  override def transform(obj: I, arg: A): Option[O] = {
    transformers.foldLeft[Option[O]](None)((maybeOutputObj, transformer) => maybeOutputObj.orElse(transformer.transform(obj, arg)))
  }
}
