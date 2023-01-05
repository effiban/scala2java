package io.github.effiban.scala2java.core.typeinference

import io.github.effiban.scala2java.core.extensions.ExtensionRegistry
import io.github.effiban.scala2java.spi.typeinferrers.{NameTypeInferrer, TypeInferrer}

import scala.meta.Term

private[typeinference] class CompositeNameTypeInferrer(theCoreInferrer: NameTypeInferrer)
                                                      (implicit extensionRegistry: ExtensionRegistry)
  extends CompositeCoreAndOthersTypeInferrer[Term.Name] with NameTypeInferrer {

  override protected def coreInferrer(): TypeInferrer[Term.Name] = theCoreInferrer

  override protected val otherInferrers: List[TypeInferrer[Term.Name]] = extensionRegistry.nameTypeInferrers
}