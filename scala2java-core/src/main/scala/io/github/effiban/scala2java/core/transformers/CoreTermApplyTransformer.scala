package io.github.effiban.scala2java.core.transformers

import io.github.effiban.scala2java.spi.transformers.TermApplyTransformer

import scala.meta.Term

private[transformers] class CoreTermApplyTransformer extends TermApplyTransformer {

  override final def transform(termApply: Term.Apply): Term.Apply = {
    // TODO - move transformations from CoreTermSelectTransformer here once desugaring is supported
    termApply
  }
}

object CoreTermApplyTransformer extends CoreTermApplyTransformer
