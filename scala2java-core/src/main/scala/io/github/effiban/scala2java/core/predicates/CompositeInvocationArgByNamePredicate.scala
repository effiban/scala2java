package io.github.effiban.scala2java.core.predicates

import io.github.effiban.scala2java.core.extensions.ExtensionRegistry
import io.github.effiban.scala2java.spi.entities.InvocationArgCoordinates
import io.github.effiban.scala2java.spi.predicates.InvocationArgByNamePredicate

class CompositeInvocationArgByNamePredicate(coreInvocationArgByNamePredicate: InvocationArgByNamePredicate)
                                           (implicit extensionRegistry: ExtensionRegistry)
  extends CompositeAtLeastOneTruePredicate0[InvocationArgCoordinates] with InvocationArgByNamePredicate {

  override val predicates: List[InvocationArgCoordinates => Boolean] = coreInvocationArgByNamePredicate +: extensionRegistry.invocationArgByNamePredicates
}
