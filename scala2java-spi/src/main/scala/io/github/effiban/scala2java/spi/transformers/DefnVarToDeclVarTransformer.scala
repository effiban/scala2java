package io.github.effiban.scala2java.spi.transformers

import io.github.effiban.scala2java.spi.entities.JavaScope.JavaScope

import scala.meta.{Decl, Defn}

/** A transformer which can transform a given [[Defn.Var]] (variable definition) into a [[Decl.Var]] (variable declaration).<br>
 * The target Java scope of the variable is also provided, so that the implementer can apply custom logic based on it.
 * For example, a data member and a local variable might require different transformation rules.<br>
 * This is useful for supporting Java frameworks that can replace a variable definition with an annotation on a variable declaration.<br>
 * For example, Mockito's `@Mock` annotation on a variable replaces the explicit instantiation of a mock object.
 */
trait DefnVarToDeclVarTransformer extends DifferentTypeTransformer1[Defn.Var, JavaScope, Decl.Var]

object DefnVarToDeclVarTransformer {
  /** The default transformer which returns `None`, indicating that no transformation is needed. */
  def Empty: DefnVarToDeclVarTransformer = (_, _) => None
}
