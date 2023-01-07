package io.github.effiban.scala2java.spi.typeinferrers

import scala.meta.{Term, Type}

trait ApplyTypeInferrer extends TypeInferrer1[Term.Apply, List[Option[Type]]]

object ApplyTypeInferrer {
  def Empty: ApplyTypeInferrer = (_, _) => None
}
