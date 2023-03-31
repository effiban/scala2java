package io.github.effiban.scala2java.spi.typeinferrers

import io.github.effiban.scala2java.spi.Scala2JavaExtension

/** A container for all extension provider hooks which are type inferrers.<br>
 * '''NOTE regarding precedence''': Type inferrers are always invoked on the original Scala code before any transformers are invoked.
 *
 * @see [[Scala2JavaExtension]]
 */
trait ExtendedTypeInferrers {

  /** Override this method if you need to apply custom logic for inferring a partial [[scala.meta.Decl.Def]] (method signature),
   * corresponding to a [[scala.meta.Term.Apply]] (method invocation).<br>
   * If the inferrers of all extensions return `None`, the tool will default to the core logic which is able to infer for
   * some of the Scala standard library methods.
   *
   * @return if overriden - an inferrer which attempts to infer a partial method signature corresponding to a [[scala.meta.Term.Apply]]
   *         otherwise - the empty inferrer which always returns the empty object (meaning nothing could be inferred)
   */
  def applyDeclDefInferrer(): ApplyDeclDefInferrer = ApplyDeclDefInferrer.Empty

  /** Override this method if you need to apply custom logic for inferring the type of a [[scala.meta.Term.Name]] (identifier).
   * If the inferrers of all extensions return `None`, the tool will default to the core logic which is able to infer for Scala standard library identifiers.<br>
   *
   * @return if overriden - an inferrer which attempts to infer the type of a [[scala.meta.Term.Name]]
   *         otherwise - the empty inferrer which always returns `None` (could not be inferred)
   */
  def nameTypeInferrer(): NameTypeInferrer = NameTypeInferrer.Empty

  /** Override this method if you need to apply custom logic for inferring the type of a [[scala.meta.Term.Select]] (qualified name).
   * If the inferrers of all extensions return `None`, the tool will default to the core logic,
   * which is able to infer for Scala standard library qualified names.<br>
   *
   * @return if overriden - an inferrer which attempts to infer the type of a [[scala.meta.Term.Select]]
   *         otherwise - the empty inferrer which always returns `None` (could not be inferred)
   */
  def selectTypeInferrer(): SelectTypeInferrer = SelectTypeInferrer.Empty
}
