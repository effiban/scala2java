package io.github.effiban.scala2java.core.extensions

import io.github.effiban.scala2java.spi.typeinferrers.{ApplyDeclDefInferrer, SelectTypeInferrer}

private[extensions] trait ExtendedTypeInferrers { this: ExtensionContainer =>

  val applyDeclDefInferrers: List[ApplyDeclDefInferrer] = extensions.map(_.applyDeclDefInferrer())

  val selectTypeInferrers: List[SelectTypeInferrer] = extensions.map(_.selectTypeInferrer())
}
