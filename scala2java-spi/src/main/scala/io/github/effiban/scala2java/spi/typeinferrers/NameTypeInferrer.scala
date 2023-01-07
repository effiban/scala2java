package io.github.effiban.scala2java.spi.typeinferrers

import scala.meta.Term

trait NameTypeInferrer extends TypeInferrer0[Term.Name]

object NameTypeInferrer {
  def Empty: NameTypeInferrer = _ => None
}
