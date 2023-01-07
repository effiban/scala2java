package io.github.effiban.scala2java.core.typeinference

import io.github.effiban.scala2java.core.extensions.ExtensionRegistry
import io.github.effiban.scala2java.spi.typeinferrers.{NameTypeInferrer, TypeInferrer0}

import scala.meta.Term

private[typeinference] class CompositeNameTypeInferrer(theCoreInferrer: NameTypeInferrer)
                                                      (implicit extensionRegistry: ExtensionRegistry)
  extends CompositeWithCoreTypeInferrer0[Term.Name] with NameTypeInferrer {

  override protected def coreInferrer(): TypeInferrer0[Term.Name] = theCoreInferrer

  override protected val otherInferrers: List[TypeInferrer0[Term.Name]] = extensionRegistry.nameTypeInferrers
}