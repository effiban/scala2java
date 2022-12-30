package io.github.effiban.scala2java.core.typeinference

import io.github.effiban.scala2java.core.extensions.ExtensionRegistry
import io.github.effiban.scala2java.spi.typeinferrers.ApplyTypeTypeInferrer

import scala.meta.{Term, Type}

private[typeinference] class CompositeApplyTypeTypeInferrer(coreApplyTypeTypeInferrer: => ApplyTypeTypeInferrer)
                                                           (implicit extensionRegistry: ExtensionRegistry) extends ApplyTypeTypeInferrer {

  private lazy val inferrers: List[ApplyTypeTypeInferrer] = extensionRegistry.applyTypeTypeInferrers :+ coreApplyTypeTypeInferrer

  override def infer(termApplyType: Term.ApplyType): Option[Type] = {
    inferrers.foldLeft[Option[Type]](None)((maybeType, inferrer) => maybeType.orElse(inferrer.infer(termApplyType)))
  }
}