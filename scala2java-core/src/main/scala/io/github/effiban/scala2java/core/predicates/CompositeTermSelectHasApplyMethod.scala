package io.github.effiban.scala2java.core.predicates

import io.github.effiban.scala2java.core.extensions.ExtensionRegistry
import io.github.effiban.scala2java.spi.predicates.TermSelectHasApplyMethod

import scala.meta.Term

class CompositeTermSelectHasApplyMethod(coreTermSelectHasApplyMethod: TermSelectHasApplyMethod)
                                       (implicit extensionRegistry: ExtensionRegistry)
  extends CompositeAtLeastOneTruePredicate0[Term.Select] with TermSelectHasApplyMethod {

  override protected val predicates: List[Term.Select => Boolean] = coreTermSelectHasApplyMethod +: extensionRegistry.termSelectHasApplyMethods
}
