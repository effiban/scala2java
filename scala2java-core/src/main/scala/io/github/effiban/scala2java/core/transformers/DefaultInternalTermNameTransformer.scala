package io.github.effiban.scala2java.core.transformers

import io.github.effiban.scala2java.spi.transformers.TermNameTransformer

import scala.meta.Term

private[transformers] class DefaultInternalTermNameTransformer(termNameTransformer: => TermNameTransformer) extends InternalTermNameTransformer {

  override def transform(termName: Term.Name): Term = termNameTransformer.transform(termName).getOrElse(termName)
}
