package io.github.effiban.scala2java.core.cleanup

import scala.meta.{Template, Tree}

trait TreeInitCleanup {
  def cleanup(tree: Tree): Tree
}

private[cleanup] class TreeInitCleanupImpl(templateInitCleanup: TemplateInitCleanup) extends TreeInitCleanup {

  override def cleanup(tree: Tree): Tree = tree.transform {
    case template: Template => templateInitCleanup.cleanup(template)
  }
}
