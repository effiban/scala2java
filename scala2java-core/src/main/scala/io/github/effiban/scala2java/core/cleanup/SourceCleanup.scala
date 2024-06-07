package io.github.effiban.scala2java.core.cleanup

import scala.meta.Source

trait SourceCleanup {
  def cleanup(source: Source): Source
}

private[cleanup] class SourceCleanupImpl(treeCleanup: TreeCleanup) extends SourceCleanup {
  
  def cleanup(source: Source): Source = {
    treeCleanup.cleanup(source).asInstanceOf[Source]
  }
}