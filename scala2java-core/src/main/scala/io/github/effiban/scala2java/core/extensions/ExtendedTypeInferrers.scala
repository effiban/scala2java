package io.github.effiban.scala2java.core.extensions

import io.github.effiban.scala2java.spi.typeinferrers.ApplyTypeTypeInferrer

private[extensions] trait ExtendedTypeInferrers { this: ExtensionContainer =>

  val applyTypeTypeInferrers: List[ApplyTypeTypeInferrer] = extensions.map(_.applyTypeTypeInferrer())

}
