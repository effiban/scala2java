package io.github.effiban.scala2java.spi.transformers

import scala.meta.Type

trait ClassNameTransformer {

  def transform(className: Type.Name): Type.Name
}

object ClassNameTransformer {
  val Identity: ClassNameTransformer = identity[Type.Name]
}
