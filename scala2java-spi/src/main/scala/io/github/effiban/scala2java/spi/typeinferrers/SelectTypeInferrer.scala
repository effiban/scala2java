package io.github.effiban.scala2java.spi.typeinferrers

import io.github.effiban.scala2java.spi.contexts.TermSelectInferenceContext

import scala.meta.Term

/** An inferrer which attempts to infer the type of a [[Term.Select]] (qualified name).<br>
 * In addition to the qualified name, the inferrer receives a context argument containing
 * additional information to make the inference more accurate (such as the type of qualifier).
 */
@deprecated
trait SelectTypeInferrer extends TypeInferrer1[Term.Select, TermSelectInferenceContext]

@deprecated
object SelectTypeInferrer {

  /** The empty inferrer which always returns `None`, meaning - cannot be inferred. */
  def Empty: SelectTypeInferrer = (_, _) => None
}
