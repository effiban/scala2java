package io.github.effiban.scala2java.core.typeinference

import io.github.effiban.scala2java.core.extensions.ExtensionRegistry
import io.github.effiban.scala2java.spi.typeinferrers.{ApplyTypeInferrer, TypeInferrer1}

import scala.meta.{Term, Type}

private[typeinference] class CompositeApplyTypeInferrer(theCoreInferrer: => ApplyTypeInferrer)
                                                       (implicit extensionRegistry: ExtensionRegistry)
  extends CompositeWithCoreTypeInferrer1[Term.Apply, List[Option[Type]]] with ApplyTypeInferrer {

  override protected def coreInferrer(): TypeInferrer1[Term.Apply, List[Option[Type]]] = theCoreInferrer

  override protected val otherInferrers: List[TypeInferrer1[Term.Apply, List[Option[Type]]]] = extensionRegistry.applyTypeInferrers
}