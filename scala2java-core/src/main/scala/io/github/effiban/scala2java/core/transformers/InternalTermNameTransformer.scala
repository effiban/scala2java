package io.github.effiban.scala2java.core.transformers

import io.github.effiban.scala2java.core.contexts.InternalTermNameTransformationContext
import io.github.effiban.scala2java.spi.transformers.TermNameTransformer

import scala.meta.Term

trait InternalTermNameTransformer {
  def transform(termName: Term.Name, context: InternalTermNameTransformationContext = InternalTermNameTransformationContext()): Term
}

private[transformers] class InternalTermNameTransformerImpl(termNameTransformer: TermNameTransformer)
  extends InternalTermNameTransformer {

  override def transform(termName: Term.Name, context: InternalTermNameTransformationContext = InternalTermNameTransformationContext()): Term = {
    termNameTransformer.transform(termName).getOrElse(termName)
  }
}
