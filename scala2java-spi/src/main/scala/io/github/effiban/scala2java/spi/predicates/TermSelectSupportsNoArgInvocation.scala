package io.github.effiban.scala2java.spi.predicates

import io.github.effiban.scala2java.spi.contexts.TermSelectInferenceContext

import scala.meta.Term

/** A predicate which determines whether a given [[Term.Select]] (qualified name) appearing in the Scala source file,
 * would be a zero-argument method invocation, when appearing by itself with no args or parentheses.<br>
 * The framework will use this to properly 'desugar' these invocations when inferring types and transforming to Java.
 */
// TODO - add ability to check by a given type as well
trait TermSelectSupportsNoArgInvocation extends ((Term.Select, TermSelectInferenceContext) => Boolean)

object TermSelectSupportsNoArgInvocation {

  /** The default predicate which returns false, meaning none of the qualified names are method invocations */
  val None: TermSelectSupportsNoArgInvocation = (_, _) => false
}