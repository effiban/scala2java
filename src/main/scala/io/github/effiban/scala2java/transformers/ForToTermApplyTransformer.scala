package io.github.effiban.scala2java.transformers


import scala.meta.Term

trait ForToTermApplyTransformer extends ForVariantToTermApplyTransformer

private[transformers] class ForToTermApplyTransformerImpl(override val patToTermParamTransformer: PatToTermParamTransformer)
  extends ForToTermApplyTransformer {

  private final val ForEachFunctionName: Term.Name = Term.Name("forEach")

  override val intermediateFunctionName: Term.Name = ForEachFunctionName
  override val finalFunctionName: Term.Name = ForEachFunctionName
}

object ForToTermApplyTransformer extends ForToTermApplyTransformerImpl(PatToTermParamTransformer)