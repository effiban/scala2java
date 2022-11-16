package io.github.effiban.scala2java.spi.transformers

import scala.meta.Defn

trait DefnDefTransformer {

  def transform(defnDef: Defn.Def): Defn.Def
}

object DefnDefTransformer {
  val Identity: DefnDefTransformer = identity[Defn.Def]
}
