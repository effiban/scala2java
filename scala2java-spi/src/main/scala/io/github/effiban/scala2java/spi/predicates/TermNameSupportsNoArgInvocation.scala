package io.github.effiban.scala2java.spi.predicates

import scala.meta.Term

/** A predicate which determines whether a given [[Term.Name]] appearing in the Scala source file,
 * would be a zero-argument method invocation, when appearing by itself with no args or parentheses.<br>
 * The framework will use this to properly 'desugar' these invocations when inferring types and transforming to Java.
 */
// TODO - add ability to check by a given type as well
trait TermNameSupportsNoArgInvocation extends (Term.Name => Boolean)

object TermNameSupportsNoArgInvocation {

  /** The default predicate which returns false, meaning none of the isolated term names are method invocations */
  val None: TermNameSupportsNoArgInvocation = _ => false
}