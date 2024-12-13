package io.github.effiban.scala2java.core.typeinference

import io.github.effiban.scala2java.core.qualifiers.QualificationContext

import scala.collection.immutable.ListMap
import scala.meta.{Template, Tree, Type}

trait EnclosingTemplateAncestorsInferrer {
  def infer(tree: Tree, context: QualificationContext): ListMap[Template, List[Type.Ref]]
}

private[typeinference] class EnclosingTemplateAncestorsInferrerImpl(enclosingTemplatesInferrer: EnclosingTemplatesInferrer,
                                                                    templateAncestorsInferrer: TemplateAncestorsInferrer)
  extends EnclosingTemplateAncestorsInferrer {

  override def infer(tree: Tree, context: QualificationContext): ListMap[Template, List[Type.Ref]] = {
    ListMap.from(
      enclosingTemplatesInferrer.infer(tree)
        .map(template => (template, templateAncestorsInferrer.infer(template, context)))
        .filterNot { case (_, ancestors) => ancestors.isEmpty }
    )
  }
}

object EnclosingTemplateAncestorsInferrer extends EnclosingTemplateAncestorsInferrerImpl(
  EnclosingTemplatesInferrer,
  TemplateAncestorsInferrer
)