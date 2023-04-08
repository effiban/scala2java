package io.github.effiban.scala2java.core.transformers

import scala.meta.Term

trait InternalTermNameTransformer {
  def transform(termName: Term.Name): Term
}
