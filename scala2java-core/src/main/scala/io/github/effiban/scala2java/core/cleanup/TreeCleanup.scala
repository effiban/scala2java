package io.github.effiban.scala2java.core.cleanup

import scala.meta.{Template, Tree}

trait TreeCleanup {
  def cleanup(tree: Tree): Tree
}

private[cleanup] class TreeCleanupImpl(templateCleanup: TemplateCleanup) extends TreeCleanup {

  override def cleanup(tree: Tree): Tree = tree.transform {
    case template: Template => templateCleanup.cleanup(template)
  }
}
