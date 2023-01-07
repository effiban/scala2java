package io.github.effiban.scala2java.spi.typeinferrers

import scala.meta.{Term, Type}

/**
 * An inferrer which attempts to infer the type of a [[Term.ApplyType]] (method invocation).<br>
 * In addition to the invocation, the inferrer will also receive a second argument which contains the
 * types of its arguments, as previously inferred by the framework (whenever possible).
 */
trait ApplyTypeInferrer extends TypeInferrer1[Term.Apply, List[Option[Type]]]

object ApplyTypeInferrer {
  def Empty: ApplyTypeInferrer = (_, _) => None
}
