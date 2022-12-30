package io.github.effiban.scala2java.spi.typeinferrers

import scala.meta.Term

trait ApplyTypeInferrer extends TypeInferrer[Term.Apply]

object ApplyTypeInferrer {
  def Empty: ApplyTypeInferrer = _ => None
}
