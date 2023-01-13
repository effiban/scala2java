package io.github.effiban.scala2java.spi.transformers

/** A generic transformer between objects of different types, with no additional arguments.
 *
 * @tparam I the type of input object
 * @tparam O the type of output object
 */
trait DifferentTypeTransformer0[I, O] {

  /** Transforms the input of type `I` into an output of type `O` when applicable.
   *
   * @param obj the input object
   * @return the output object, or `None` if no transformation required
   */
  def transform(obj: I): Option[O]
}
