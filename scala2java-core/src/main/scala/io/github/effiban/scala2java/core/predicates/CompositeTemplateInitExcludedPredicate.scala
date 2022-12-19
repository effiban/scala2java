package io.github.effiban.scala2java.core.predicates

import io.github.effiban.scala2java.core.extensions.ExtensionRegistry
import io.github.effiban.scala2java.spi.predicates.TemplateInitExcludedPredicate

import scala.meta.Init

class CompositeTemplateInitExcludedPredicate(coreTemplateInitExcludedPredicate: TemplateInitExcludedPredicate)
                                            (implicit extensionRegistry: ExtensionRegistry)
  extends CompositeAtLeastOneTruePredicate[Init] with TemplateInitExcludedPredicate {

  override protected val predicates: List[Init => Boolean] = coreTemplateInitExcludedPredicate +: extensionRegistry.templateInitExcludedPredicates
}
