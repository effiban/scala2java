package io.github.effiban.scala2java.spi.predicates

import io.github.effiban.scala2java.spi.Scala2JavaExtension

import scala.meta.Term

/** A container for all extension provider hooks which are predicates.
 *
 * @see [[Scala2JavaExtension]]
 */
trait ExtendedPredicates {

  /** Override this method if you need to exclude [[scala.meta.Init]]-s (parents) of a class/trait/object in the corresponding Java type.<br>
   *
   * @return if overriden - a predicate which determines whether to exclude a [[scala.meta.Init]]<br>
   *         otherwise - the default predicate which doesn't exclude anything<br>
   */
  def templateInitExcludedPredicate(): TemplateInitExcludedPredicate = TemplateInitExcludedPredicate.None


  /** Override this method if you need to specify that a given [[Term.Select]] appearing in the Scala source file,
   * has an `apply()` method (whether defined implicitly or explicitly).<br>
   * This is true of all case classes and functions, and also some companion objects.<br>
   * The framework will use this in order to properly identify invocations where the `apply()`
   * does not appear explicitly and be able to infer their types and also transform them properly into Java.
   */
  def termSelectHasApplyMethod(): TermSelectHasApplyMethod = TermSelectHasApplyMethod.None

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
