package io.github.effiban.scala2java.core.extractors

import scala.meta.{Init, Type}


trait InitTypeRefExtractor {
  def extract(init: Init): Option[Type.Ref]
}

private[extractors] class InitTypeRefExtractorImpl(typeRefExtractor: TypeRefExtractor) extends InitTypeRefExtractor {
  def extract(init: Init): Option[Type.Ref] = typeRefExtractor.extract(init.tpe)
}

object InitTypeRefExtractor extends InitTypeRefExtractorImpl(TypeRefExtractor)
