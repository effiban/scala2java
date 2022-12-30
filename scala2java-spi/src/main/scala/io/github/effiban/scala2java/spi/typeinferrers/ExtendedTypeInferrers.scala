package io.github.effiban.scala2java.spi.typeinferrers

import io.github.effiban.scala2java.spi.Scala2JavaExtension

/** A container for all extension provider hooks which are [[io.github.effiban.scala2java.spi.typeinferrers.TypeInferrer]]-s.<br>
 * '''NOTE regarding precedence''': Type inferrers are always invoked on the original Scala code before any transformers are invoked.
 *
 * @see [[Scala2JavaExtension]]
 */
trait ExtendedTypeInferrers {

  /** Override this method if you need to apply custom logic for inferring the type of a [[scala.meta.Term.ApplyType]] (parameterized type application).<br>
   * If the inferrers of all the extensions return `None`, the tool will default to the core logic as follows:<br>
   * It first checks the type of the `fun` part, and then (if resolved) appends the type argument.<br>
   * For example, if the input is `foo[T]` and `foo` is of type `Foo`, then the inferred type will be `Foo[T]`
   *
   * @return if overriden - an inferrer which attempts to infer the type of a [[scala.meta.Term.ApplyType]]
   *         otherwise - the empty inferrer which always returns `None` (could not be inferred)
   */
  def applyTypeTypeInferrer(): ApplyTypeTypeInferrer = ApplyTypeTypeInferrer.Empty
}
