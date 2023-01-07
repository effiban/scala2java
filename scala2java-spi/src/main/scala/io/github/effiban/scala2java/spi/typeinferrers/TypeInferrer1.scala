package io.github.effiban.scala2java.spi.typeinferrers

import scala.meta.Type

/** An inferrer which attempts to infer the type of a Scala language element when not explicitly provided.<br>
 * It receives one additional argument besides the element to be inferred.<br>
 * Typically this would be used to provide additional type information about nested elements that was
 * inferred in advance by the framework (and is necessary for properly inferring this one).
 *
 * @tparam T the type of element to be inferred
 * @tparam A the type of additional argument to the inferrer
 */
trait TypeInferrer1[T, A] {

  /** Attempts to infer the type of the given object, which is usually a subtype of [[scala.meta.Term]]
   *
   * @param obj the language element whose type is to be inferred
   * @param arg the additional argument to the inferrer
   * @return the inferred type, if it can be determined;<br>
   *         [[Type.AnonymousName]] if the element is untyped by definition;<br>
   *         `None` if the type cannot be determined
   */
  def infer(obj: T, arg: A): Option[Type]
}
