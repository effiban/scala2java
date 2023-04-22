package io.github.effiban.scala2java.spi.transformers

import scala.meta.Term

/** A transformer which can modify a given Scala [[Term.Name]] (identifier) when appearing in the context of
 * an evaulated expression, for example:
 * - return value
 * - method argument
 * - RHS of an assignment
 * - RHS of a 'case' clause
 */

trait TermNameTransformer extends DifferentTypeTransformer0[Term.Name, Term]

object TermNameTransformer {

  /** The default transformer which returns `None`, indicating that no transformation is needed. */
  def Empty: TermNameTransformer = _ => None
}