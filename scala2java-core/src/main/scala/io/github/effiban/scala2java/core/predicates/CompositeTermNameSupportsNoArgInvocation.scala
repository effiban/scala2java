package io.github.effiban.scala2java.core.predicates

import io.github.effiban.scala2java.core.extensions.ExtensionRegistry
import io.github.effiban.scala2java.spi.predicates.TermNameSupportsNoArgInvocation

import scala.meta.Term

class CompositeTermNameSupportsNoArgInvocation(coreTermNameSupportsNoArgInvocation: TermNameSupportsNoArgInvocation)
                                              (implicit extensionRegistry: ExtensionRegistry)
  extends CompositeAtLeastOneTruePredicate[Term.Name] with TermNameSupportsNoArgInvocation {

  override protected val predicates: List[Term.Name => Boolean] =
    coreTermNameSupportsNoArgInvocation +: extensionRegistry.termNameSupportsNoArgInvocations
}
