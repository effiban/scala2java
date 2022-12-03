package io.github.effiban.scala2java.spi.transformers

trait FileNameTransformer {

  def transform(fileName: String): String
}

object FileNameTransformer {
  val Identity: FileNameTransformer = identity[String]
}
