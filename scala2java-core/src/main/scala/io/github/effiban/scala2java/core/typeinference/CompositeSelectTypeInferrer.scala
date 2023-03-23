package io.github.effiban.scala2java.core.typeinference

import io.github.effiban.scala2java.core.extensions.ExtensionRegistry
import io.github.effiban.scala2java.spi.contexts.TermSelectInferenceContext
import io.github.effiban.scala2java.spi.typeinferrers.{SelectTypeInferrer, TypeInferrer1}

import scala.meta.Term

private[typeinference] class CompositeSelectTypeInferrer(theCoreInferrer: => SelectTypeInferrer)
                                                        (implicit extensionRegistry: ExtensionRegistry)
  extends CompositeWithCoreTypeInferrer1[Term.Select, TermSelectInferenceContext] with SelectTypeInferrer {

  override protected def coreInferrer(): TypeInferrer1[Term.Select, TermSelectInferenceContext] = theCoreInferrer

  override protected val otherInferrers: List[TypeInferrer1[Term.Select, TermSelectInferenceContext]] = extensionRegistry.selectTypeInferrers
}