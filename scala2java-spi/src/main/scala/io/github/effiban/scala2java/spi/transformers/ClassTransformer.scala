package io.github.effiban.scala2java.spi.transformers

import scala.meta.Defn

trait ClassTransformer extends SameTypeTransformer[Defn.Class]

object ClassTransformer {
  val Identity: ClassTransformer = identity[Defn.Class]
}
