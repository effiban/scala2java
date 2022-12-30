package io.github.effiban.scala2java.core.typeinference

import io.github.effiban.scala2java.core.extensions.ExtensionRegistry
import io.github.effiban.scala2java.spi.typeinferrers.{ApplyTypeInferrer, TypeInferrer}

import scala.meta.Term

private[typeinference] class CompositeApplyTypeInferrer(theCoreInferrer: => ApplyTypeInferrer)
                                                       (implicit extensionRegistry: ExtensionRegistry)
  extends CompositeCoreAndOthersTypeInferrer[Term.Apply] with ApplyTypeInferrer {

  override protected def coreInferrer(): TypeInferrer[Term.Apply] = theCoreInferrer

  override protected val otherInferrers: List[TypeInferrer[Term.Apply]] = extensionRegistry.applyTypeInferrers
}