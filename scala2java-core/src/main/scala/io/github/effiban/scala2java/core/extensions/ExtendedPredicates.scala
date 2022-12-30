package io.github.effiban.scala2java.core.extensions

import io.github.effiban.scala2java.spi.predicates.{ImporterExcludedPredicate, TemplateInitExcludedPredicate}

private[extensions] trait ExtendedPredicates { this: ExtensionContainer =>

  val importerExcludedPredicates: List[ImporterExcludedPredicate] = extensions.map(_.importerExcludedPredicate())

  val templateInitExcludedPredicates: List[TemplateInitExcludedPredicate] = extensions.map(_.templateInitExcludedPredicate())
}
