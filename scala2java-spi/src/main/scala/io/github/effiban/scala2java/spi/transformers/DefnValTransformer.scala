package io.github.effiban.scala2java.spi.transformers

import io.github.effiban.scala2java.spi.entities.JavaScope.JavaScope

import scala.meta.Defn

/** A transformer which can modify a given Scala [[Defn.Val]] (`val`, immutable variable definition) */
trait DefnValTransformer {

  /** Transforms a given [[Defn.Val]] (`val`, immutable variable definition).
   * The target Java scope of the variable is also provided, so that the implementer can apply custom logic based on it.
   * For example, a data member and a local variable might require different transformation rules.
   *
   * @param defnVal   the [[Defn.Val]] to be transformed
   * @param javaScope the target scope of the variable in the generated Java code
   * @return the transformed [[Defn.Val]]
   */
  def transform(defnVal: Defn.Val, javaScope: JavaScope): Defn.Val
}

object DefnValTransformer {

  /** The default transformer which returns the [[Defn.Val]] unchanged, indicating that no transformation is needed. */
  val Identity: DefnValTransformer = (defnVal, _) => defnVal
}
