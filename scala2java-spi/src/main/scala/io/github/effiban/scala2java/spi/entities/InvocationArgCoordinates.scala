package io.github.effiban.scala2java.spi.entities

import scala.meta.{Term, Tree}

/** Uniquely identifies or "locates" an argument of an invocation.<br>
 * An invocation in Scala can be any of the following:
 *   - A method or constructor invocation
 *   - An infix method invocation
 *   - An annotation with arguments
 *   - A superclass initializer
 *   - An anonymous class instantiation
 *
 * @param invocation the invocation which contains the argument
 * @param maybeName the name of the argument, if explicitly specified; `None` otherwise
 * @param index the index of the argument in the invocation
 *
 * @see [[io.github.effiban.scala2java.spi.predicates.InvocationArgByNamePredicate]]
 */
case class InvocationArgCoordinates(invocation: Tree,
                                    maybeName: Option[Term.Name] = None,
                                    index: Int)
