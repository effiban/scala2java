package io.github.effiban.scala2java.spi.typeinferrers

import scala.meta.Type

/** An inferrer which attempts to infer the type of a Scala language element when not explicitly provided */
trait TypeInferrer[T] {

  /** Attempts to infer the type of the given object, which is usually a subtype of [[scala.meta.Term]]
   *
   * @param obj the language element whose type is to be inferred
   * @return the inferred type, if it can be determined;<br>
   *         [[Type.AnonymousName]] if the element is untyped by definition;<br>
   *         `None` if the type cannot be determined
   */
  def infer(obj: T): Option[Type]
}
