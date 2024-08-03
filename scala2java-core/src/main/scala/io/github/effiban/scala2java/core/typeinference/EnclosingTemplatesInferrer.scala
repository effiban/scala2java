package io.github.effiban.scala2java.core.typeinference

import scala.meta.{Template, Tree}

trait EnclosingTemplatesInferrer {
  def infer(tree: Tree): List[Template]
}

private[typeinference] class EnclosingTemplatesInferrerImpl(innermostEnclosingTemplateInferrer: InnermostEnclosingTemplateInferrer)
  extends EnclosingTemplatesInferrer {

  final override def infer(tree: Tree): List[Template] = {
    val innermostAsList = innermostEnclosingTemplateInferrer.infer(tree).toList
    innermostAsList ++ innermostAsList.flatMap(infer)
  }
}

object EnclosingTemplatesInferrer extends EnclosingTemplatesInferrerImpl(InnermostEnclosingTemplateInferrer)