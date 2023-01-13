package io.github.effiban.scala2java.spi.transformers

import scala.meta.Defn

/** A transformer which can modify a given Scala [[Defn.Class]] */
trait ClassTransformer extends SameTypeTransformer0[Defn.Class]

object ClassTransformer {
  /** The default transformer which returns the [[Defn.Class]] unchanged, indicating that no transformation is needed. */
  val Identity: ClassTransformer = identity[Defn.Class]
}
