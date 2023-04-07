package io.github.effiban.scala2java.core.predicates

import io.github.effiban.scala2java.core.extensions.ExtensionRegistry
import io.github.effiban.scala2java.spi.predicates.TermNameHasApplyMethod

import scala.meta.Term

class CompositeTermNameHasApplyMethod(coreTermNameHasApplyMethod: TermNameHasApplyMethod)
                                     (implicit extensionRegistry: ExtensionRegistry)
  extends CompositeAtLeastOneTruePredicate[Term.Name] with TermNameHasApplyMethod {

  override protected val predicates: List[Term.Name => Boolean] = coreTermNameHasApplyMethod +: extensionRegistry.termNameHasApplyMethods
}
