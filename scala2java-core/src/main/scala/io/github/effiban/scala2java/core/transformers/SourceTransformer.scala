package io.github.effiban.scala2java.core.transformers

import scala.meta.{Source, Tree}

trait SourceTransformer {
  def transform(source: Source): Source
}

private[transformers] class SourceTransformerImpl(treeTransformer: => TreeTransformer) extends SourceTransformer {

  override def transform(source: Source): Source = {
      treeTransformer.transform(source).asInstanceOf[Source]
  }
}
