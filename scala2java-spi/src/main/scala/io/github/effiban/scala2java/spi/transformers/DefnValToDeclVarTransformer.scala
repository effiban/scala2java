package io.github.effiban.scala2java.spi.transformers

import io.github.effiban.scala2java.spi.entities.JavaScope.JavaScope

import scala.meta.{Decl, Defn}

/** A transformer which can transform a given [[Defn.Val]] (definition of `val` - immutable variable)
 * into a [[Decl.Var]] (declaration of 'var' - mutable variable).
 * This is useful for supporting Java frameworks that can replace a variable definition with an annotation on a variable declaration.
 * For example, Mockito's `@Mock` annotation on a variable replaces the explicit instantiation of a mock object.
 */
trait DefnValToDeclVarTransformer {

  /** Transforms a given [[Defn.Val]] into a [[Decl.Var]] where applicable.
   * The target Java scope of the variable is also provided, so that the implementer can apply custom logic based on it.
   * For example, a data member and a local variable might require different transformation rules.
   *
   * @param defnVal the [[Defn.Val]] to be transformed
   * @param javaScope the target scope of the variable in the generated Java code
   * @return the transformed [[Decl.Var]] where applicable, or `None` if no transformation required
   */
  def transform(defnVal: Defn.Val, javaScope: JavaScope): Option[Decl.Var]
}

object DefnValToDeclVarTransformer {
  /** The default transformer which returns `None`, indicating that no transformation is needed. */
  def Empty: DefnValToDeclVarTransformer = (_, _) => None
}
