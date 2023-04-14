package io.github.effiban.scala2java.spi.predicates

import io.github.effiban.scala2java.spi.Scala2JavaExtension

import scala.meta.Term

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


  /** Override this method if you need to indicate that an invocation argument is be passed by-name
   *
   * @return if overriden - a predicate which determines whether a given invocation argument is passed by-name
   *         otherwise - the default predicate which always returns `false`, meaning passed by-value<br>
   */
  def invocationArgByNamePredicate(): InvocationArgByNamePredicate = InvocationArgByNamePredicate.Default

  /** Override this method if you need to exclude [[scala.meta.Init]]-s (parents) of a class/trait/object in the corresponding Java type.<br>
   *
   * @return if overriden - a predicate which determines whether to exclude a [[scala.meta.Init]]<br>
   *         otherwise - the default predicate which doesn't exclude anything<br>
   */
  def templateInitExcludedPredicate(): TemplateInitExcludedPredicate = TemplateInitExcludedPredicate.None


  /** Override this method if you need to specify that a given [[Term.Name]] appearing in the Scala source file,
   * has an `apply()` method (whether defined implicitly or explicitly).<br>
   * This is true of all case classes and functions, and also some companion objects.<br>
   * The framework will use this in order to properly identify invocations where the `apply()`
   * does not appear explicitly and be able to infer their types and also transform them properly into Java.
   */
  def termNameHasApplyMethod(): TermNameHasApplyMethod = TermNameHasApplyMethod.None

  /** Override this method if you need to specify that a given [[Term.Name]] appearing in the Scala source file,
   * is actually a no-argument invocation to a method.<br>
   * In Scala some methods are defined without parentheses and must be called that way, while others have repeated (variable) argument lists
   * and may also be optionally invoked without parentheses.<br>
   * For example: The expression `println` (with no parentheses) is a call to the `println()` method.<br>
   * The framework will use this in order to properly identify such invocations, so as to be able to infer their types
   * and also transform them properly into Java.
   */
  def termNameSupportsNoArgInvocation(): TermNameSupportsNoArgInvocation = TermNameSupportsNoArgInvocation.None

  /** Override this method if you need to specify that a given [[Term.Select]] (qualified name) appearing in the Scala source file,
   * is actually a no-argument invocation to a method.<br>
   * In Scala some methods are defined without parentheses and must be called that way, while others have repeated (variable) argument lists
   * and may also be optionally invoked without parentheses.<br>
   * For example: `List.empty` (without parentheses) is a call to a method returning an empty list.<br>
   * The framework will use this in order to properly identify these invocations, so as to be able to infer their types
   * and also transform them properly into Java.
   */
  def termSelectSupportsNoArgInvocation(): TermSelectSupportsNoArgInvocation = TermSelectSupportsNoArgInvocation.None
}
