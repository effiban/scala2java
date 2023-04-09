package io.github.effiban.scala2java.core.transformers

import io.github.effiban.scala2java.spi.contexts.TermSelectTransformationContext
import io.github.effiban.scala2java.spi.transformers.TermSelectTransformer

import scala.meta.Term

private[transformers] class DefaultInternalTermSelectTransformer(termSelectTransformer: => TermSelectTransformer)
  extends InternalTermSelectTransformer {

  override def transform(termSelect: Term.Select, context: TermSelectTransformationContext = TermSelectTransformationContext()): Term =
    termSelectTransformer.transform(termSelect, context).getOrElse(termSelect)
}
