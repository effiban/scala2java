package io.github.effiban.scala2java.spi.transformers

trait FileNameTransformer extends SameTypeTransformer[String]

object FileNameTransformer {
  val Identity: FileNameTransformer = identity[String]
}
