package io.github.effiban.scala2java.core.cleanup

import scala.meta.Source

trait SourceInitCleanup {
  def cleanup(source: Source): Source
}

class SourceInitCleanupImpl(treeInitCleanup: TreeInitCleanup) extends SourceInitCleanup {

  def cleanup(source: Source): Source = {
    treeInitCleanup.cleanup(source).asInstanceOf[Source]
  }
}
