package io.github.effiban.scala2java.core.extensions

import io.github.effiban.scala2java.spi.typeinferrers.{ApplyTypeInferrer, ApplyTypeTypeInferrer, NameTypeInferrer, SelectTypeInferrer}

private[extensions] trait ExtendedTypeInferrers { this: ExtensionContainer =>

  val applyTypeInferrers: List[ApplyTypeInferrer] = extensions.map(_.applyTypeInferrer())

  val applyTypeTypeInferrers: List[ApplyTypeTypeInferrer] = extensions.map(_.applyTypeTypeInferrer())

  val nameTypeInferrers: List[NameTypeInferrer] = extensions.map(_.nameTypeInferrer())

  val selectTypeInferrers: List[SelectTypeInferrer] = extensions.map(_.selectTypeInferrer())
}
