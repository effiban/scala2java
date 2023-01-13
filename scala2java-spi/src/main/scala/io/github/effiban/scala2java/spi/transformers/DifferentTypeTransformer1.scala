package io.github.effiban.scala2java.spi.transformers

/** A generic transformer with one input object and one additional argument
 *
 * @tparam I the type of input object
 * @tparam A the type of input argument
 * @tparam O the type of output object
 */
trait DifferentTypeTransformer1[I, A, O] {

  /** Transforms the input of type `I` into an output of type `O` when applicable.
   *
   * @param obj the input object
   * @param arg the additional input argument
   * @return the output object, or `None` if no transformation required
   */
  def transform(obj: I, arg: A): Option[O]
}
