package io.github.effiban.scala2java.core.typeinference

import scala.annotation.tailrec
import scala.meta.{Member, Template, Tree}

trait InnermostEnclosingTemplateInferrer {
  def infer(tree: Tree, maybeEnclosingMemberName: Option[String] = None): Option[Template]
}

object InnermostEnclosingTemplateInferrer extends InnermostEnclosingTemplateInferrer {

  override def infer(tree: Tree, maybeEnclosingMemberName: Option[String] = None): Option[Template] = {
    tree.parent.flatMap(parent => inferInner(parent, maybeEnclosingMemberName))
  }

  @tailrec
  private def inferInner(tree: Tree, maybeEnclosingMemberName: Option[String] = None): Option[Template] = {
    (tree, tree.parent, maybeEnclosingMemberName) match {
      case (template: Template, Some(parent: Member), Some(enclosingMemberName))
        if enclosingMemberName == parent.name.value => Some(template)
      case (template: Template, Some(_), None) => Some(template)
      case (_, Some(parent), maybeEncMemName) => inferInner(parent, maybeEncMemName)
      case _ => None
    }
  }
}