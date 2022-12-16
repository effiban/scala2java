package io.github.effiban.scala2java.spi.transformers

/** A generic transformer between objects of the same type.
 *
 * @tparam T the type of object to transform
 */
trait SameTypeTransformer[T] {

  /** Transforms the input into another object of the same type.
   *
   * @param obj the object to transform
   * @return the transformed object
   */
  def transform(obj: T): T
}
