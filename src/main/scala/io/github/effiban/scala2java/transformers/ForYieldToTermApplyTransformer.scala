package io.github.effiban.scala2java.transformers

import scala.meta.Term

trait ForYieldToTermApplyTransformer extends ForVariantToTermApplyTransformer

private[transformers] class ForYieldToTermApplyTransformerImpl(override val patToTermParamTransformer: PatToTermParamTransformer)
  extends ForYieldToTermApplyTransformer {

  override val intermediateFunctionName: Term.Name = Term.Name("flatMap")
  override val finalFunctionName: Term.Name = Term.Name("map")
}

object ForYieldToTermApplyTransformer extends ForYieldToTermApplyTransformerImpl(PatToTermParamTransformer)