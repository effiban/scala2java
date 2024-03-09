package io.github.effiban.scala2java.core.extensions

import io.github.effiban.scala2java.spi.predicates._

private[extensions] trait ExtendedPredicates { this: ExtensionContainer =>

  val templateInitExcludedPredicates: List[TemplateInitExcludedPredicate] = extensions.map(_.templateInitExcludedPredicate())

  val termNameHasApplyMethods: List[TermNameHasApplyMethod] = extensions.map(_.termNameHasApplyMethod())

  val termSelectHasApplyMethods: List[TermSelectHasApplyMethod] = extensions.map(_.termSelectHasApplyMethod())

  val termNameSupportsNoArgInvocations: List[TermNameSupportsNoArgInvocation] = extensions.map(_.termNameSupportsNoArgInvocation())

  val termSelectSupportsNoArgInvocations: List[TermSelectSupportsNoArgInvocation] = extensions.map(_.termSelectSupportsNoArgInvocation())
}
