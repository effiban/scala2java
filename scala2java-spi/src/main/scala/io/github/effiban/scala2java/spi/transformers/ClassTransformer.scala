package io.github.effiban.scala2java.spi.transformers

import scala.meta.Defn

trait ClassTransformer {

  def transform(classDef: Defn.Class): Defn.Class
}

object ClassTransformer {
  val Identity: ClassTransformer = identity[Defn.Class]
}
