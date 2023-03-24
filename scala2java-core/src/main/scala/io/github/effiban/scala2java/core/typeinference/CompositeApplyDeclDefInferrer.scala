package io.github.effiban.scala2java.core.typeinference

import io.github.effiban.scala2java.core.extensions.ExtensionRegistry
import io.github.effiban.scala2java.spi.contexts.TermApplyInferenceContext
import io.github.effiban.scala2java.spi.entities.PartialDeclDef
import io.github.effiban.scala2java.spi.typeinferrers.ApplyDeclDefInferrer

import scala.meta.Term

private[typeinference] class CompositeApplyDeclDefInferrer(coreInferrer: => ApplyDeclDefInferrer)
                                                          (implicit extensionRegistry: ExtensionRegistry) extends ApplyDeclDefInferrer {

  private val extensionInferrers: List[ApplyDeclDefInferrer] = extensionRegistry.applyDeclDefInferrers

  private lazy val inferrers: List[ApplyDeclDefInferrer] = extensionInferrers :+ coreInferrer

  override def infer(termApply: Term.Apply, context: TermApplyInferenceContext): PartialDeclDef = {
    inferrers.foldLeft(PartialDeclDef())((partialDeclDef, inferrer) => partialDeclDef match {
      case aPartialDeclDef if aPartialDeclDef.isEmpty => inferrer.infer(termApply, context)
      case aPartialDeclDef => aPartialDeclDef
    })
  }
}