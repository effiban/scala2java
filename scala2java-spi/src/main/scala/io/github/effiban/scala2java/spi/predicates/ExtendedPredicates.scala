package io.github.effiban.scala2java.spi.predicates

import io.github.effiban.scala2java.spi.Scala2JavaExtension

/** A container for all extension provider hooks which are predicates.
 *
 * @see [[Scala2JavaExtension]]
 */
trait ExtendedPredicates {

  /** Override this method if you need to exclude [[scala.meta.Importer]]-s (import statements) that exist in the Scala file,
   * but do not belong in the generated Java file.<br>
   * '''NOTE regarding precedence''': This predicate will be invoked before [[io.github.effiban.scala2java.spi.transformers.ExtendedTransformers.importerTransformer]].<br>
   * This means that if this extension, or any other one, wishes to modify an importer that is excluded by this predicate -
   * that modification will be ignored.
   *
   * @see [[ImporterExcludedPredicate]] for more information on how the framework will invoke this predicate.<br>
   * @return if overriden - a transformer which excludes importers<br>
   *         otherwise - the default transformer which does not exclude anything<br>
   */
  def importerExcludedPredicate(): ImporterExcludedPredicate = ImporterExcludedPredicate.None

  /** Override this method if you need to exclude [[scala.meta.Init]]-s (parents) of a class/trait/object in the corresponding Java type.<br>
   *
   * @return if overriden - a predicate which determines whether to exclude a [[scala.meta.Init]]<br>
   *         otherwise - the default predicate which doesn't exclude anything<br>
   */
  def templateInitExcludedPredicate(): TemplateInitExcludedPredicate = TemplateInitExcludedPredicate.None
}
