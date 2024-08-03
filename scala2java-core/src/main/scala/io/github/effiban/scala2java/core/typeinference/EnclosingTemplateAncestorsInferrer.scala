package io.github.effiban.scala2java.core.typeinference

import io.github.effiban.scala2java.core.collectors.TemplateAncestorsCollector

import scala.meta.{Template, Tree, Type}

trait EnclosingTemplateAncestorsInferrer {
  def infer(tree: Tree): Map[Template, List[Type.Ref]]
}

private[typeinference] class EnclosingTemplateAncestorsInferrerImpl(enclosingTemplatesInferrer: EnclosingTemplatesInferrer,
                                                                    templateAncestorsCollector: TemplateAncestorsCollector)
  extends EnclosingTemplateAncestorsInferrer {

  override def infer(tree: Tree): Map[Template, List[Type.Ref]] = {
    enclosingTemplatesInferrer.infer(tree)
      .map(template => (template, templateAncestorsCollector.collect(template)))
      .filterNot { case (_, ancestors) => ancestors.isEmpty }
      .toMap
  }
}

object EnclosingTemplateAncestorsInferrer extends EnclosingTemplateAncestorsInferrerImpl(
  EnclosingTemplatesInferrer,
  TemplateAncestorsCollector
)