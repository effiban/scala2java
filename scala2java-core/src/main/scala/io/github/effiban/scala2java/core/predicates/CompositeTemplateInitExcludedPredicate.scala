package io.github.effiban.scala2java.core.predicates

import io.github.effiban.scala2java.core.extensions.ExtensionRegistry
import io.github.effiban.scala2java.spi.predicates.TemplateInitExcludedPredicate

import scala.meta.Init

class CompositeTemplateInitExcludedPredicate(coreTemplateInitExcludedPredicate: TemplateInitExcludedPredicate)
                                            (implicit extensionRegistry: ExtensionRegistry) extends TemplateInitExcludedPredicate {

  override def apply(init: Init): Boolean = {
    val predicates = coreTemplateInitExcludedPredicate +: extensionRegistry.templateInitExcludedPredicates
    predicates.forall(_.apply(init))
  }
}
