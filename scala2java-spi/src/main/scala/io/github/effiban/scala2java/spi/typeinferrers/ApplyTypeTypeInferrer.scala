package io.github.effiban.scala2java.spi.typeinferrers

import scala.meta.Term

/** An inferrer which attempts to infer the type of a given [[Term.ApplyType]] (parametrized type application) */
trait ApplyTypeTypeInferrer extends TypeInferrer0[Term.ApplyType]

object ApplyTypeTypeInferrer {

  /** The empty inferrer which always returns `None`, meaning - cannot be inferred. */
  val Empty: ApplyTypeTypeInferrer = _ => None
}
