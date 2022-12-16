package io.github.effiban.scala2java.spi.transformers

import scala.meta.Defn

trait DefnDefTransformer extends SameTypeTransformer[Defn.Def]

object DefnDefTransformer {
  val Identity: DefnDefTransformer = identity[Defn.Def]
}
