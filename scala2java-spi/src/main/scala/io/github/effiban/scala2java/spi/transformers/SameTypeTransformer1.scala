package io.github.effiban.scala2java.spi.transformers

/** A generic transformer between objects of the same type with one additional argument
 *
 * @tparam T the type of object
 * @tparam A the type of input argument
 */
trait SameTypeTransformer1[T, A] {

  /** Transforms the input into another object of the same type.
   *
   * @param obj the object to transform
   * @param arg the additional argument
   * @return the transformed object
   */
  def transform(obj: T, arg: A): T
}
