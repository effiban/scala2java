package io.github.effiban.scala2java.spi.typeinferrers

import scala.meta.Term

/** An inferrer which attempts to infer the declared type of a [[Term.Name]] */
trait NameTypeInferrer extends TypeInferrer0[Term.Name]

object NameTypeInferrer {

  /** The empty inferrer which always returns `None`, meaning - cannot be inferred. */
  def Empty: NameTypeInferrer = _ => None
}
