package io.github.effiban.scala2java.core.extensions

import io.github.effiban.scala2java.spi.predicates._

private[extensions] trait ExtendedPredicates { this: ExtensionContainer =>

  val termSelectHasApplyMethods: List[TermSelectHasApplyMethod] = extensions.map(_.termSelectHasApplyMethod())

  val termSelectSupportsNoArgInvocations: List[TermSelectSupportsNoArgInvocation] = extensions.map(_.termSelectSupportsNoArgInvocation())
}
