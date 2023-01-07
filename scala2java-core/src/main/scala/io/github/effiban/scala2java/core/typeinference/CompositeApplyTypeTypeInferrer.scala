package io.github.effiban.scala2java.core.typeinference

import io.github.effiban.scala2java.core.extensions.ExtensionRegistry
import io.github.effiban.scala2java.spi.typeinferrers.{ApplyTypeTypeInferrer, TypeInferrer0}

import scala.meta.Term

private[typeinference] class CompositeApplyTypeTypeInferrer(theCoreInferrer: => ApplyTypeTypeInferrer)
                                                           (implicit extensionRegistry: ExtensionRegistry)
  extends CompositeWithCoreTypeInferrer0[Term.ApplyType] with ApplyTypeTypeInferrer {

  override protected def coreInferrer(): TypeInferrer0[Term.ApplyType] = theCoreInferrer

  override protected val otherInferrers: List[TypeInferrer0[Term.ApplyType]] = extensionRegistry.applyTypeTypeInferrers
}