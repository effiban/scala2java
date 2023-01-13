package io.github.effiban.scala2java.spi.transformers

import scala.meta.Defn

/** A transformer which can modify a given Scala [[Defn.Def]] (method definition) */
trait DefnDefTransformer extends SameTypeTransformer0[Defn.Def]

object DefnDefTransformer {
  /** The default transformer which returns the [[Defn.Def]] unchanged, indicating that no transformation is needed. */
  val Identity: DefnDefTransformer = identity[Defn.Def]
}
