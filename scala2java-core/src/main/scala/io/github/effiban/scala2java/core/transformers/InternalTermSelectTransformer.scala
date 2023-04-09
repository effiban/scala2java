package io.github.effiban.scala2java.core.transformers

import io.github.effiban.scala2java.spi.contexts.TermSelectTransformationContext

import scala.meta.Term

trait InternalTermSelectTransformer {
  def transform(termSelect: Term.Select, context: TermSelectTransformationContext = TermSelectTransformationContext()): Term
}
