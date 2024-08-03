package io.github.effiban.scala2java.core.typeinference

import io.github.effiban.scala2java.core.collectors.TemplateAncestorsCollector

import scala.meta.{Template, Term, Tree, Type}

trait InnermostEnclosingTemplateAncestorsInferrer {
  def infer(tree: Tree, maybeEnclosingMemberName: Option[String] = None): List[Type.Ref]
}

private[typeinference] class InnermostEnclosingTemplateAncestorsInferrerImpl(innermostEnclosingTemplateInferrer: InnermostEnclosingTemplateInferrer,
                                                                             templateAncestorsCollector: TemplateAncestorsCollector)
  extends InnermostEnclosingTemplateAncestorsInferrer {

  override def infer(tree: Tree, maybeEnclosingMemberName: Option[String] = None): List[Type.Ref] = {
    innermostEnclosingTemplateInferrer.infer(tree, maybeEnclosingMemberName)
      .toList
      .flatMap(templateAncestorsCollector.collect)
  }
}

object InnermostEnclosingTemplateAncestorsInferrer extends InnermostEnclosingTemplateAncestorsInferrerImpl(
  InnermostEnclosingTemplateInferrer,
  TemplateAncestorsCollector
)