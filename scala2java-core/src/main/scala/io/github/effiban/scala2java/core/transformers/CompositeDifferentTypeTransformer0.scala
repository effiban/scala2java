package io.github.effiban.scala2java.core.transformers

import io.github.effiban.scala2java.spi.transformers.DifferentTypeTransformer0

trait CompositeDifferentTypeTransformer0[I, O] extends DifferentTypeTransformer0[I, O] {

  protected val transformers: List[DifferentTypeTransformer0[I, O]]

  override def transform(obj: I): Option[O] =
    transformers.foldLeft[Option[O]](None)((maybeOutputObj, transformer) => maybeOutputObj.orElse(transformer.transform(obj)))
}
