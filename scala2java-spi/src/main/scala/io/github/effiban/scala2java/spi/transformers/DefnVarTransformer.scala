package io.github.effiban.scala2java.spi.transformers

import io.github.effiban.scala2java.spi.entities.JavaScope.JavaScope

import scala.meta.Defn

/** A transformer which can modify a given Scala [[Defn.Var]] (variable definition).<br>
 * The target Java scope of the variable is also provided, so that the implementer can apply custom logic based on it.
 * For example, a data member and a local variable might require different transformation rules.
 */
trait DefnVarTransformer extends SameTypeTransformer1[Defn.Var, JavaScope]

object DefnVarTransformer {

  /** The default transformer which returns the [[Defn.Var]] unchanged, indicating that no transformation is needed. */
  val Identity: DefnVarTransformer = (defnVar, _) => defnVar
}
