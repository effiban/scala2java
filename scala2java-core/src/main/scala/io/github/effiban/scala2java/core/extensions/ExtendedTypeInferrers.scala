package io.github.effiban.scala2java.core.extensions

import io.github.effiban.scala2java.spi.typeinferrers.{ApplyDeclDefInferrer, ApplyTypeTypeInferrer, NameTypeInferrer, SelectTypeInferrer}

private[extensions] trait ExtendedTypeInferrers { this: ExtensionContainer =>

  val applyDeclDefInferrers: List[ApplyDeclDefInferrer] = extensions.map(_.applyDeclDefInferrer())

  val applyTypeTypeInferrers: List[ApplyTypeTypeInferrer] = extensions.map(_.applyTypeTypeInferrer())

  val nameTypeInferrers: List[NameTypeInferrer] = extensions.map(_.nameTypeInferrer())

  val selectTypeInferrers: List[SelectTypeInferrer] = extensions.map(_.selectTypeInferrer())
}
