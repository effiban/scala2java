package io.github.effiban.scala2java.core.typeinference

import scala.meta.{Member, Tree}

trait EnclosingMemberPathsInferrer {
  def infer(tree: Tree): List[List[Member]]
}

private[typeinference] class EnclosingMemberPathsInferrerImpl(innermostEnclosingMemberPathInferrer: InnermostEnclosingMemberPathInferrer)
  extends EnclosingMemberPathsInferrer {

  override def infer(tree: Tree): List[List[Member]] = {
    val fullMemberPath = innermostEnclosingMemberPathInferrer.infer(tree)
    fullMemberPath
      .indices
      .map(idx => fullMemberPath.slice(0, idx + 1))
      .toList
  }
}

object EnclosingMemberPathsInferrer extends EnclosingMemberPathsInferrerImpl(InnermostEnclosingMemberPathInferrer)