package io.github.effiban.scala2java.core.typeinference

import io.github.effiban.scala2java.core.extensions.ExtensionRegistry
import io.github.effiban.scala2java.spi.typeinferrers.{ApplyTypeTypeInferrer, TypeInferrer}

import scala.meta.Term

private[typeinference] class CompositeApplyTypeTypeInferrer(theCoreInferrer: => ApplyTypeTypeInferrer)
                                                           (implicit extensionRegistry: ExtensionRegistry)
  extends CompositeCoreAndOthersTypeInferrer[Term.ApplyType] with ApplyTypeTypeInferrer {

  override protected def coreInferrer(): TypeInferrer[Term.ApplyType] = theCoreInferrer

  override protected val otherInferrers: List[TypeInferrer[Term.ApplyType]] = extensionRegistry.applyTypeTypeInferrers
}