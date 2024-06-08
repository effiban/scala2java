package io.github.effiban.scala2java.core.typeinference

import scala.annotation.tailrec
import scala.meta.Defn.Trait
import scala.meta.{Defn, Member, Pkg, Tree}

trait InnermostEnclosingMemberPathInferrer {
  def infer(tree: Tree, maybeEnclosingTypeName: Option[String] = None): List[Member]
}

object InnermostEnclosingMemberPathInferrer extends InnermostEnclosingMemberPathInferrer {

  override def infer(tree: Tree, maybeEnclosingMemberName: Option[String] = None): List[Member] = {
    inferOne(tree, maybeEnclosingMemberName).toList
      .flatten(innermostEncloser => infer(innermostEncloser) :+ innermostEncloser)

  }

  @tailrec
  private def inferOne(tree: Tree, maybeEnclosingMemberName: Option[String] = None): Option[Member] = {
    (tree.parent, maybeEnclosingMemberName) match {
      case (Some(parentMember@(_: Defn.Class | _: Trait | _: Defn.Object | _: Pkg)), Some(enclosingMemberName))
        if enclosingMemberName == parentMember.asInstanceOf[Member].name.value => Some(parentMember.asInstanceOf[Member])
      case (Some(parentMember@(_: Defn.Class | _: Trait | _: Defn.Object | _: Pkg)), Some(enclosingMemberName)) =>
        inferOne(parentMember, Some(enclosingMemberName))
      case (Some(parentMember@(_: Defn.Class | _: Trait | _: Defn.Object | _: Pkg)), _) => Some(parentMember.asInstanceOf[Member])
      case (Some(parent: Tree), maybeEncMemName) => inferOne(parent, maybeEncMemName)
      case _ => None
    }
  }
}