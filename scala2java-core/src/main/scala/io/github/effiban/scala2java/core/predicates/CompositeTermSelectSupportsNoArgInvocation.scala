package io.github.effiban.scala2java.core.predicates

import io.github.effiban.scala2java.core.extensions.ExtensionRegistry
import io.github.effiban.scala2java.spi.predicates.TermSelectSupportsNoArgInvocation

import scala.meta.Term

class CompositeTermSelectSupportsNoArgInvocation(coreTermSelectSupportsNoArgInvocation: TermSelectSupportsNoArgInvocation)
                                                (implicit extensionRegistry: ExtensionRegistry)
  extends CompositeAtLeastOneTruePredicate[Term.Select] with TermSelectSupportsNoArgInvocation {

  override protected val predicates: List[Term.Select => Boolean] =
    coreTermSelectSupportsNoArgInvocation +: extensionRegistry.termSelectSupportsNoArgInvocations()
}
